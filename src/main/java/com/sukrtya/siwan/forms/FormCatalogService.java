package com.sukrtya.siwan.forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.sukrtya.siwan.beneficiary.BeneficiaryFormResponseRepository;
import com.sukrtya.siwan.forms.dto.FormDetail;
import com.sukrtya.siwan.forms.dto.FormQuestionView;
import com.sukrtya.siwan.forms.dto.FormSummary;
import com.sukrtya.siwan.forms.dto.FormUpsertRequest;
import com.sukrtya.siwan.forms.dto.OptionSetView;
import com.sukrtya.siwan.forms.dto.QuestionOptionView;

/** Admin-facing CRUD for the form catalog and read access for option sets. */
@Service
public class FormCatalogService {

	private final FormRepository formRepository;
	private final FormQuestionRepository formQuestionRepository;
	private final OptionSetRepository optionSetRepository;
	private final OptionValueRepository optionValueRepository;
	private final BeneficiaryFormResponseRepository responseRepository;

	public FormCatalogService(
			FormRepository formRepository,
			FormQuestionRepository formQuestionRepository,
			OptionSetRepository optionSetRepository,
			OptionValueRepository optionValueRepository,
			BeneficiaryFormResponseRepository responseRepository) {
		this.formRepository = formRepository;
		this.formQuestionRepository = formQuestionRepository;
		this.optionSetRepository = optionSetRepository;
		this.optionValueRepository = optionValueRepository;
		this.responseRepository = responseRepository;
	}

	@Transactional(readOnly = true)
	public List<FormSummary> listForms() {
		List<Form> forms = formRepository.findAllByOrderBySequenceAscNameAsc();
		Map<Long, Long> counts = countQuestionsByForm(forms);
		List<FormSummary> out = new ArrayList<>(forms.size());
		for (Form f : forms) {
			out.add(FormSummary.of(f, counts.getOrDefault(f.getId(), 0L)));
		}
		return out;
	}

	@Transactional(readOnly = true)
	public FormDetail getByCode(String code) {
		Form form = requireFormByCode(code);
		List<FormQuestion> questions = formQuestionRepository.findByFormIdOrderBySequenceAscFaQidAsc(form.getId());
		Map<String, List<QuestionOptionView>> optionsByCode = loadOptionsForQuestions(questions);

		List<FormQuestionView> views = new ArrayList<>(questions.size());
		for (FormQuestion q : questions) {
			List<QuestionOptionView> opts = q.getOptionSetCode() != null
					? optionsByCode.getOrDefault(q.getOptionSetCode(), List.of())
					: null;
			views.add(FormQuestionView.of(q, opts));
		}
		return FormDetail.of(form, views);
	}

	/**
	 * Single round trip per distinct option-set code referenced in this form (typically 0..5 sets
	 * across all questions), keeping the admin endpoint fast even for 100+ question forms.
	 */
	private Map<String, List<QuestionOptionView>> loadOptionsForQuestions(List<FormQuestion> questions) {
		Map<String, List<QuestionOptionView>> out = new HashMap<>();
		for (FormQuestion q : questions) {
			String code = q.getOptionSetCode();
			if (code == null || out.containsKey(code)) {
				continue;
			}
			OptionSet set = optionSetRepository.findByCodeIgnoreCase(code).orElse(null);
			if (set == null) {
				out.put(code, List.of());
				continue;
			}
			List<QuestionOptionView> values = optionValueRepository
					.findByOptionSetIdOrderBySequenceAscValueAsc(set.getId())
					.stream()
					.map(QuestionOptionView::of)
					.toList();
			out.put(code, values);
		}
		return out;
	}

	@Transactional
	public FormSummary create(FormUpsertRequest req) {
		String code = normaliseCode(req.code());
		if (formRepository.existsByCodeIgnoreCase(code)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Form with code '" + code + "' already exists.");
		}
		Form form = new Form();
		form.setCode(code);
		form.setName(req.name().trim());
		form.setSequence(req.sequence() != null ? req.sequence() : nextSequence());
		form.setPrerequisiteCode(validatePrerequisiteCode(code, req.prerequisiteCode()));
		form.setDescription(req.description());
		form.setActive(req.active() == null || req.active());
		Form saved = formRepository.save(form);
		return FormSummary.of(saved, 0L);
	}

	@Transactional
	public FormSummary update(String code, FormUpsertRequest req) {
		Form form = requireFormByCode(code);
		String newCode = normaliseCode(req.code());
		if (!form.getCode().equalsIgnoreCase(newCode) && formRepository.existsByCodeIgnoreCase(newCode)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Form with code '" + newCode + "' already exists.");
		}
		form.setCode(newCode);
		form.setName(req.name().trim());
		if (req.sequence() != null) {
			form.setSequence(req.sequence());
		}
		form.setPrerequisiteCode(validatePrerequisiteCode(newCode, req.prerequisiteCode()));
		form.setDescription(req.description());
		if (req.active() != null) {
			form.setActive(req.active());
		}
		Form saved = formRepository.save(form);
		long count = formQuestionRepository.findByFormIdOrderBySequenceAscFaQidAsc(saved.getId()).size();
		return FormSummary.of(saved, count);
	}

	@Transactional
	public FormSummary setActive(String code, boolean active) {
		Form form = requireFormByCode(code);
		form.setActive(active);
		Form saved = formRepository.save(form);
		long count = formQuestionRepository.findByFormIdOrderBySequenceAscFaQidAsc(saved.getId()).size();
		return FormSummary.of(saved, count);
	}

	/**
	 * Permanently removes a form catalog row and its questions ({@code form_question} cascades).
	 * Beneficiary responses and downstream forms that list this code as a prerequisite block
	 * deletion so historical data and visit chains stay intact.
	 */
	@Transactional
	public void deletePermanently(String code) {
		Form form = requireFormByCode(code);

		long responseCount = responseRepository.countByFormId(form.getId());
		if (responseCount > 0) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"Cannot delete form '" + form.getCode() + "': " + responseCount
							+ " beneficiary response(s) exist. Deactivate instead (DELETE without permanent=true),"
							+ " or remove/archive responses first.");
		}

		List<Form> dependents = formRepository.findByPrerequisiteCodeIgnoreCase(form.getCode()).stream()
				.filter(other -> !other.getId().equals(form.getId()))
				.toList();
		if (!dependents.isEmpty()) {
			String codes = dependents.stream().map(Form::getCode).sorted().reduce((a, b) -> a + ", " + b).orElse("");
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"Cannot delete form '" + form.getCode() + "': other forms list it as prerequisite: "
							+ codes + ". Update or delete those forms first.");
		}

		formRepository.delete(form);
	}

	@Transactional(readOnly = true)
	public List<OptionSetView> listOptionSets() {
		List<OptionSet> sets = optionSetRepository.findAllByOrderByCodeAsc();
		List<OptionSetView> out = new ArrayList<>(sets.size());
		for (OptionSet os : sets) {
			out.add(OptionSetView.of(os, optionValueRepository.findByOptionSetIdOrderBySequenceAscValueAsc(os.getId())));
		}
		return out;
	}

	private Form requireFormByCode(String code) {
		return formRepository.findByCodeIgnoreCase(normaliseCode(code))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Form '" + code + "' not found."));
	}

	private static String normaliseCode(String raw) {
		if (raw == null || raw.trim().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Form code is required.");
		}
		return raw.trim().toUpperCase(Locale.ROOT);
	}

	private static String normaliseCodeNullable(String raw) {
		if (raw == null || raw.trim().isEmpty()) {
			return null;
		}
		return raw.trim().toUpperCase(Locale.ROOT);
	}

	private static String validatePrerequisiteCode(String formCode, String prerequisiteCodeRaw) {
		String prerequisite = normaliseCodeNullable(prerequisiteCodeRaw);
		if (prerequisite != null && prerequisite.equalsIgnoreCase(formCode)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"prerequisiteCode cannot be the same as the form code ('" + formCode + "')."
							+ " Use the form that must be completed first (e.g. ANC4 before PNC), or omit it for BASIC.");
		}
		return prerequisite;
	}

	private int nextSequence() {
		return formRepository.findAll().stream()
				.mapToInt(Form::getSequence)
				.max()
				.orElse(0) + 10;
	}

	private Map<Long, Long> countQuestionsByForm(List<Form> forms) {
		// Avoid N+1 by issuing one count query per form; small N (rarely > 20) so this is fine.
		Map<Long, Long> out = new HashMap<>();
		for (Form f : forms) {
			out.put(f.getId(), (long) formQuestionRepository.findByFormIdOrderBySequenceAscFaQidAsc(f.getId()).size());
		}
		return out;
	}
}
