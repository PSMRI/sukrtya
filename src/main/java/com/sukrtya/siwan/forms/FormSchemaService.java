package com.sukrtya.siwan.forms;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.sukrtya.siwan.beneficiary.Beneficiary;
import com.sukrtya.siwan.beneficiary.BeneficiaryFormResponse;
import com.sukrtya.siwan.beneficiary.BeneficiaryFormResponseRepository;
import com.sukrtya.siwan.beneficiary.BeneficiaryRepository;
import com.sukrtya.siwan.forms.dto.ComputedSpec;
import com.sukrtya.siwan.forms.dto.FormSchemaResponse;
import com.sukrtya.siwan.forms.dto.FormSummary;
import com.sukrtya.siwan.forms.dto.QuestionOptionView;
import com.sukrtya.siwan.forms.dto.QuestionSchema;
import com.sukrtya.siwan.forms.dto.ResponseStatusView;
import com.sukrtya.siwan.forms.dto.SkipRule;

/**
 * Materialises a dynamic form schema for the frontend: questions in display order, options
 * expanded inline, skip-rule targets resolved to question codes, computed-field formulas
 * pre-evaluated when a beneficiary's source values are known, and any existing answers
 * echoed back for edit flows.
 */
@Service
public class FormSchemaService {

	private final FormRepository formRepository;
	private final FormQuestionRepository formQuestionRepository;
	private final OptionSetRepository optionSetRepository;
	private final OptionValueRepository optionValueRepository;
	private final BeneficiaryRepository beneficiaryRepository;
	private final BeneficiaryFormResponseRepository responseRepository;

	public FormSchemaService(
			FormRepository formRepository,
			FormQuestionRepository formQuestionRepository,
			OptionSetRepository optionSetRepository,
			OptionValueRepository optionValueRepository,
			BeneficiaryRepository beneficiaryRepository,
			BeneficiaryFormResponseRepository responseRepository) {
		this.formRepository = formRepository;
		this.formQuestionRepository = formQuestionRepository;
		this.optionSetRepository = optionSetRepository;
		this.optionValueRepository = optionValueRepository;
		this.beneficiaryRepository = beneficiaryRepository;
		this.responseRepository = responseRepository;
	}

	@Transactional(readOnly = true)
	public FormSchemaResponse buildSchema(String formCode, Long beneficiaryId) {
		Form form = formRepository.findByCodeIgnoreCase(formCode)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Form '" + formCode + "' not found."));
		if (!form.isActive()) {
			throw new ResponseStatusException(HttpStatus.GONE, "Form '" + formCode + "' is currently disabled.");
		}

		List<FormQuestion> questions = formQuestionRepository.findByFormIdOrderBySequenceAscFaQidAsc(form.getId());

		// Pre-load option sets (one lookup per distinct code in this form).
		Map<String, List<QuestionOptionView>> optionsByCode = loadOptionsForForm(questions);

		// Map faQid -> question code so we can resolve skip_to_qid targets.
		Map<Integer, String> qidToCode = new HashMap<>();
		for (FormQuestion q : questions) {
			qidToCode.put(q.getFaQid(), q.getCode());
		}

		// Beneficiary context: existing answers, BASIC answers (for cross-form prefill + computed source values).
		Beneficiary beneficiary = null;
		Map<String, Object> existingAnswers = Collections.emptyMap();
		Map<String, Object> sourceValues = new LinkedHashMap<>();
		ResponseStatusView responseStatus = ResponseStatusView.none();

		if (beneficiaryId != null) {
			beneficiary = beneficiaryRepository.findById(beneficiaryId)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Beneficiary not found."));

			// Pull every response for this beneficiary so we can prefill computed sources from any form.
			List<BeneficiaryFormResponse> all = responseRepository.findByBeneficiaryId(beneficiary.getId());
			for (BeneficiaryFormResponse r : all) {
				if (r.getForm().getId().equals(form.getId())) {
					existingAnswers = r.getAnswers() != null ? r.getAnswers() : Collections.emptyMap();
					responseStatus = new ResponseStatusView(
							true, r.getStatus(), r.getFormVersion(), r.getSubmittedAt(), r.getUpdatedAt());
				}
				if (r.getAnswers() != null) {
					sourceValues.putAll(r.getAnswers());
				}
			}
			// Promote a few typed fields from the beneficiary record so computed fields work even
			// before any form responses exist (e.g. EDD = beneficiary.lmp_date + 280).
			projectBeneficiaryFields(beneficiary, sourceValues);
		}

		List<QuestionSchema> out = new ArrayList<>(questions.size());
		for (FormQuestion q : questions) {
			List<QuestionOptionView> opts = q.getOptionSetCode() != null
					? optionsByCode.getOrDefault(q.getOptionSetCode(), List.of())
					: null;

			SkipRule skip = null;
			if (q.getSkipValue() != null && q.getSkipToQid() != null) {
				skip = new SkipRule(q.getSkipValue(), q.getSkipToQid(), qidToCode.get(q.getSkipToQid()));
			}

			ComputedSpec computed = null;
			if (q.getComputedKind() == ComputedKind.DATE_OFFSET) {
				String resolved = resolveDateOffset(q, sourceValues);
				computed = new ComputedSpec(q.getComputedKind(), q.getComputedSourceCode(),
						q.getComputedOffsetDays(), resolved);
			}

			Object currentValue = existingAnswers.get(q.getCode());

			out.add(new QuestionSchema(
					q.getCode(),
					q.getFaQid(),
					q.getLabelEn(),
					q.getLabelHi(),
					q.getAnswerType(),
					q.isMandatory(),
					q.getMinValue(),
					q.getMaxValue(),
					q.getDefaultValue(),
					q.getRemarks(),
					opts,
					skip,
					computed,
					currentValue,
					q.getSequence()));
		}

		FormSummary summary = FormSummary.of(form, questions.size());
		return new FormSchemaResponse(
				summary,
				out,
				sourceValues,
				beneficiary != null ? beneficiary.getId() : null,
				responseStatus);
	}

	private Map<String, List<QuestionOptionView>> loadOptionsForForm(List<FormQuestion> questions) {
		Map<String, List<QuestionOptionView>> out = new HashMap<>();
		for (FormQuestion q : questions) {
			String code = q.getOptionSetCode();
			if (code == null || out.containsKey(code)) {
				continue;
			}
			Optional<OptionSet> setOpt = optionSetRepository.findByCodeIgnoreCase(code);
			if (setOpt.isEmpty()) {
				out.put(code, List.of());
				continue;
			}
			List<QuestionOptionView> values = optionValueRepository
					.findByOptionSetIdOrderBySequenceAscValueAsc(setOpt.get().getId())
					.stream()
					.map(QuestionOptionView::of)
					.toList();
			out.put(code, values);
		}
		return out;
	}

	private static void projectBeneficiaryFields(Beneficiary b, Map<String, Object> sourceValues) {
		// Hot beneficiary columns are exposed under a stable "beneficiary.*" namespace plus the
		// canonical BASIC question codes so computed fields can reference either.
		putIfPresent(sourceValues, "beneficiary.fullName", b.getFullName());
		putIfPresent(sourceValues, "beneficiary.husbandName", b.getHusbandName());
		putIfPresent(sourceValues, "beneficiary.mobilePhone", b.getMobilePhone());
		putIfPresent(sourceValues, "beneficiary.abhaId", b.getAbhaId());
		putIfPresent(sourceValues, "beneficiary.village", b.getVillage());
		putIfPresent(sourceValues, "beneficiary.age", b.getAge());
		putIfPresent(sourceValues, "beneficiary.lmpDate", b.getLmpDate() != null ? b.getLmpDate().toString() : null);
		putIfPresent(sourceValues, "beneficiary.eddDate", b.getEddDate() != null ? b.getEddDate().toString() : null);
		putIfPresent(sourceValues, "beneficiary.status", b.getStatus() != null ? b.getStatus().name() : null);
	}

	private static void putIfPresent(Map<String, Object> map, String key, Object value) {
		if (value != null) {
			map.put(key, value);
		}
	}

	/**
	 * Evaluate a DATE_OFFSET field by reading the source question's answer and adding the offset.
	 * Falls back to the beneficiary's {@code lmp_date} column when the source code happens to point
	 * at a BASIC LMP question that hasn't been re-saved as a form response yet.
	 */
	private static String resolveDateOffset(FormQuestion q, Map<String, Object> sourceValues) {
		if (q.getComputedSourceCode() == null || q.getComputedOffsetDays() == null) {
			return null;
		}
		Object raw = sourceValues.get(q.getComputedSourceCode());
		if (raw == null) {
			raw = sourceValues.get("beneficiary.lmpDate");
		}
		if (raw == null) {
			return null;
		}
		LocalDate base;
		try {
			base = LocalDate.parse(String.valueOf(raw));
		}
		catch (DateTimeParseException ex) {
			return null;
		}
		return base.plusDays(q.getComputedOffsetDays()).toString();
	}
}
