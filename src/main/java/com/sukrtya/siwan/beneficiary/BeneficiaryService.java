package com.sukrtya.siwan.beneficiary;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.sukrtya.siwan.beneficiary.dto.BeneficiaryDetail;
import com.sukrtya.siwan.beneficiary.dto.BeneficiaryFormResponseView;
import com.sukrtya.siwan.beneficiary.dto.BeneficiarySummary;
import com.sukrtya.siwan.beneficiary.dto.BeneficiaryUpsertRequest;
import com.sukrtya.siwan.beneficiary.dto.FormResponseStatusSummary;
import com.sukrtya.siwan.beneficiary.dto.FormSubmitRequest;
import com.sukrtya.siwan.forms.Form;
import com.sukrtya.siwan.forms.FormQuestion;
import com.sukrtya.siwan.forms.FormQuestionRepository;
import com.sukrtya.siwan.forms.FormRepository;
import com.sukrtya.siwan.master.FacilityAssignmentStatus;
import com.sukrtya.siwan.master.FacilityWorkerAssignment;
import com.sukrtya.siwan.master.FacilityWorkerAssignmentRepository;
import com.sukrtya.siwan.master.HealthWorkerRole;
import com.sukrtya.siwan.portal.PortalRole;
import com.sukrtya.siwan.portal.PortalUser;
import com.sukrtya.siwan.portal.PortalUserFacility;
import com.sukrtya.siwan.portal.PortalUserFacilityRepository;
import com.sukrtya.siwan.portal.PortalUserRepository;

/**
 * Pregnant-woman registry + form response writer.
 *
 * <p>Scope rules:
 * <ul>
 *   <li>{@code ADMIN}: unrestricted (sees / writes all facilities).</li>
 *   <li>Anyone else (data collectors): only beneficiaries at facilities they are mapped to via
 *       {@code portal_user_facility} (active mappings).</li>
 * </ul>
 *
 * <p>All write paths run through {@link FormResponseValidator} so the JSONB answers payload is
 * structurally consistent with the configured {@link FormQuestion} rows before it ever hits the DB.
 */
@Service
public class BeneficiaryService {

	public static final String BASIC_FORM_CODE = "BASIC";

	private final BeneficiaryRepository beneficiaryRepository;
	private final BeneficiaryFormResponseRepository responseRepository;
	private final BeneficiaryCodeService codeService;
	private final FormResponseValidator validator;
	private final FormRepository formRepository;
	private final FormQuestionRepository formQuestionRepository;
	private final FacilityWorkerAssignmentRepository assignmentRepository;
	private final PortalUserRepository portalUserRepository;
	private final PortalUserFacilityRepository portalUserFacilityRepository;

	public BeneficiaryService(
			BeneficiaryRepository beneficiaryRepository,
			BeneficiaryFormResponseRepository responseRepository,
			BeneficiaryCodeService codeService,
			FormResponseValidator validator,
			FormRepository formRepository,
			FormQuestionRepository formQuestionRepository,
			FacilityWorkerAssignmentRepository assignmentRepository,
			PortalUserRepository portalUserRepository,
			PortalUserFacilityRepository portalUserFacilityRepository) {
		this.beneficiaryRepository = beneficiaryRepository;
		this.responseRepository = responseRepository;
		this.codeService = codeService;
		this.validator = validator;
		this.formRepository = formRepository;
		this.formQuestionRepository = formQuestionRepository;
		this.assignmentRepository = assignmentRepository;
		this.portalUserRepository = portalUserRepository;
		this.portalUserFacilityRepository = portalUserFacilityRepository;
	}

	// ============================================================================
	// Reads
	// ============================================================================

	@Transactional(readOnly = true)
	public List<BeneficiarySummary> listUnderAsha(Long ashaAssignmentId, Authentication authentication) {
		FacilityWorkerAssignment asha = requireAshaAssignment(ashaAssignmentId);
		PortalUser user = requireUser(authentication);
		requireFacilityAccess(user, asha.getFacility().getId());

		List<Form> forms = formRepository.findAllByOrderBySequenceAscNameAsc();
		List<Beneficiary> beneficiaries = beneficiaryRepository.findByAshaAssignmentIdOrderByCreatedAtDesc(ashaAssignmentId);
		Map<Long, Map<Long, BeneficiaryFormResponse>> responsesByBeneficiary = loadResponsesGrouped(beneficiaries);

		List<BeneficiarySummary> out = new ArrayList<>(beneficiaries.size());
		for (Beneficiary b : beneficiaries) {
			out.add(BeneficiarySummary.of(b, buildFormStatusGrid(forms, responsesByBeneficiary.getOrDefault(b.getId(), Map.of()))));
		}
		return out;
	}

	@Transactional(readOnly = true)
	public BeneficiaryDetail getDetail(Long beneficiaryId, Authentication authentication) {
		Beneficiary b = requireBeneficiary(beneficiaryId);
		PortalUser user = requireUser(authentication);
		requireFacilityAccess(user, b.getFacility().getId());

		List<Form> forms = formRepository.findAllByOrderBySequenceAscNameAsc();
		Map<Long, BeneficiaryFormResponse> byFormId = loadResponsesGrouped(List.of(b)).getOrDefault(b.getId(), Map.of());
		List<FormResponseStatusSummary> grid = buildFormStatusGrid(forms, byFormId);

		Map<String, Object> basicAnswers = Collections.emptyMap();
		for (BeneficiaryFormResponse r : byFormId.values()) {
			if (BASIC_FORM_CODE.equalsIgnoreCase(r.getForm().getCode())) {
				basicAnswers = r.getAnswers() != null ? r.getAnswers() : Collections.emptyMap();
				break;
			}
		}

		return new BeneficiaryDetail(BeneficiarySummary.of(b, grid), basicAnswers, grid);
	}

	@Transactional(readOnly = true)
	public BeneficiaryFormResponseView getFormResponse(Long beneficiaryId, String formCode, Authentication authentication) {
		Beneficiary b = requireBeneficiary(beneficiaryId);
		PortalUser user = requireUser(authentication);
		requireFacilityAccess(user, b.getFacility().getId());

		Form form = requireForm(formCode);
		BeneficiaryFormResponse r = responseRepository.findByBeneficiaryIdAndFormId(b.getId(), form.getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Form '" + form.getCode() + "' has not been submitted for this beneficiary yet."));
		return BeneficiaryFormResponseView.of(r);
	}

	// ============================================================================
	// Writes
	// ============================================================================

	@Transactional
	public BeneficiarySummary register(Long ashaAssignmentId, BeneficiaryUpsertRequest request, Authentication authentication) {
		FacilityWorkerAssignment asha = requireAshaAssignment(ashaAssignmentId);
		PortalUser user = requireUser(authentication);
		requireFacilityAccess(user, asha.getFacility().getId());

		Form basic = requireForm(BASIC_FORM_CODE);
		List<FormQuestion> questions = formQuestionRepository.findByFormIdOrderBySequenceAscFaQidAsc(basic.getId());
		Map<String, Object> answers = request.answers() != null ? new LinkedHashMap<>(request.answers()) : new LinkedHashMap<>();

		List<String> errors = validator.validate(questions, answers);
		if (!errors.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Validation failed: " + errors);
		}

		Beneficiary b = new Beneficiary();
		b.setFacility(asha.getFacility());
		b.setAshaAssignment(asha);
		b.setCode(codeService.nextCode());
		b.setCreatedBy(user.getId());
		applyBeneficiaryProjections(b, questions, answers);
		if (isBlank(b.getFullName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"BASIC answers must include a question whose label resolves to the pregnant woman's name.");
		}
		rejectIfDuplicate(asha.getFacility().getId(), b.getMobilePhone(), b.getAbhaId(), null);
		Beneficiary saved = beneficiaryRepository.save(b);

		BeneficiaryFormResponse response = new BeneficiaryFormResponse();
		response.setBeneficiary(saved);
		response.setForm(basic);
		response.setFormVersion(basic.getVersion());
		response.setAnswers(answers);
		response.setStatus(request.statusOrDefault());
		response.setSubmittedById(user.getId());
		if (response.getStatus() == ResponseStatus.SUBMITTED) {
			response.setSubmittedAt(Instant.now());
		}
		responseRepository.save(response);

		List<Form> forms = formRepository.findAllByOrderBySequenceAscNameAsc();
		Map<Long, BeneficiaryFormResponse> byFormId = Map.of(basic.getId(), response);
		return BeneficiarySummary.of(saved, buildFormStatusGrid(forms, byFormId));
	}

	@Transactional
	public BeneficiarySummary updateBasic(Long beneficiaryId, BeneficiaryUpsertRequest request, Authentication authentication) {
		Beneficiary b = requireBeneficiary(beneficiaryId);
		PortalUser user = requireUser(authentication);
		requireFacilityAccess(user, b.getFacility().getId());

		Form basic = requireForm(BASIC_FORM_CODE);
		List<FormQuestion> questions = formQuestionRepository.findByFormIdOrderBySequenceAscFaQidAsc(basic.getId());
		Map<String, Object> answers = request.answers() != null ? new LinkedHashMap<>(request.answers()) : new LinkedHashMap<>();

		List<String> errors = validator.validate(questions, answers);
		if (!errors.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Validation failed: " + errors);
		}

		applyBeneficiaryProjections(b, questions, answers);
		rejectIfDuplicate(b.getFacility().getId(), b.getMobilePhone(), b.getAbhaId(), b.getId());
		beneficiaryRepository.save(b);

		BeneficiaryFormResponse response = responseRepository.findByBeneficiaryIdAndFormId(b.getId(), basic.getId())
				.orElseGet(() -> {
					BeneficiaryFormResponse created = new BeneficiaryFormResponse();
					created.setBeneficiary(b);
					created.setForm(basic);
					return created;
				});
		response.setAnswers(answers);
		response.setFormVersion(basic.getVersion());
		response.setStatus(request.statusOrDefault());
		response.setSubmittedById(user.getId());
		if (response.getStatus() == ResponseStatus.SUBMITTED && response.getSubmittedAt() == null) {
			response.setSubmittedAt(Instant.now());
		}
		responseRepository.save(response);

		List<Form> forms = formRepository.findAllByOrderBySequenceAscNameAsc();
		Map<Long, BeneficiaryFormResponse> byFormId = loadResponsesGrouped(List.of(b)).getOrDefault(b.getId(), Map.of());
		return BeneficiarySummary.of(b, buildFormStatusGrid(forms, byFormId));
	}

	@Transactional
	public BeneficiaryFormResponseView submitForm(
			Long beneficiaryId,
			String formCode,
			FormSubmitRequest request,
			Authentication authentication) {
		if (BASIC_FORM_CODE.equalsIgnoreCase(formCode)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Use PUT /api/beneficiaries/{id} to update BASIC; this endpoint is for follow-up forms only.");
		}
		Beneficiary b = requireBeneficiary(beneficiaryId);
		PortalUser user = requireUser(authentication);
		requireFacilityAccess(user, b.getFacility().getId());

		Form form = requireForm(formCode);
		List<FormQuestion> questions = formQuestionRepository.findByFormIdOrderBySequenceAscFaQidAsc(form.getId());
		Map<String, Object> answers = request.answers() != null ? new LinkedHashMap<>(request.answers()) : new LinkedHashMap<>();

		List<String> errors = validator.validate(questions, answers);
		if (!errors.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Validation failed: " + errors);
		}

		BeneficiaryFormResponse response = responseRepository.findByBeneficiaryIdAndFormId(b.getId(), form.getId())
				.orElseGet(() -> {
					BeneficiaryFormResponse created = new BeneficiaryFormResponse();
					created.setBeneficiary(b);
					created.setForm(form);
					return created;
				});
		response.setAnswers(answers);
		response.setFormVersion(form.getVersion());
		response.setStatus(request.statusOrDefault());
		response.setSubmittedById(user.getId());
		if (response.getStatus() == ResponseStatus.SUBMITTED && response.getSubmittedAt() == null) {
			response.setSubmittedAt(Instant.now());
		}
		responseRepository.save(response);
		return BeneficiaryFormResponseView.of(response);
	}

	// ============================================================================
	// Internals
	// ============================================================================

	private Map<Long, Map<Long, BeneficiaryFormResponse>> loadResponsesGrouped(List<Beneficiary> beneficiaries) {
		Map<Long, Map<Long, BeneficiaryFormResponse>> out = new HashMap<>();
		for (Beneficiary b : beneficiaries) {
			List<BeneficiaryFormResponse> rs = responseRepository.findByBeneficiaryIdOrderedByForm(b.getId());
			Map<Long, BeneficiaryFormResponse> byFormId = new LinkedHashMap<>();
			for (BeneficiaryFormResponse r : rs) {
				byFormId.put(r.getForm().getId(), r);
			}
			out.put(b.getId(), byFormId);
		}
		return out;
	}

	private static List<FormResponseStatusSummary> buildFormStatusGrid(
			List<Form> forms,
			Map<Long, BeneficiaryFormResponse> byFormId) {
		List<FormResponseStatusSummary> out = new ArrayList<>(forms.size());
		for (Form f : forms) {
			BeneficiaryFormResponse r = byFormId.get(f.getId());
			boolean active = f.isActive();
			if (r == null) {
				FormCollectionState state = active ? FormCollectionState.NOT_STARTED : FormCollectionState.FORM_INACTIVE;
				out.add(new FormResponseStatusSummary(
						f.getCode(), f.getName(), f.getSequence(), f.getPrerequisiteCode(),
						active, state,
						false, null, null, null, null));
			}
			else {
				FormCollectionState state = resolveCollectionState(active, r.getStatus());
				out.add(new FormResponseStatusSummary(
						f.getCode(), f.getName(), f.getSequence(), f.getPrerequisiteCode(),
						active, state,
						true, r.getStatus(), r.getFormVersion(), r.getSubmittedAt(), r.getUpdatedAt()));
			}
		}
		return out;
	}

	private static FormCollectionState resolveCollectionState(boolean formActive, ResponseStatus status) {
		if (!formActive) {
			return FormCollectionState.FORM_INACTIVE_HAS_DATA;
		}
		if (status == ResponseStatus.SUBMITTED) {
			return FormCollectionState.SUBMITTED;
		}
		return FormCollectionState.DRAFT;
	}

	private void applyBeneficiaryProjections(Beneficiary b, List<FormQuestion> basicQuestions, Map<String, Object> answers) {
		Map<String, FormQuestion> byLabel = indexQuestionsBySimplifiedLabel(basicQuestions);
		assignTextIfMatch(byLabel, answers, b::setFullName, "nameofpregnantwomen", "nameofpregnantwoman", "namepregnantwomen");
		assignTextIfMatch(byLabel, answers, b::setHusbandName, "nameofhusband", "husbandname", "husband");
		assignTextIfMatch(byLabel, answers, b::setMobilePhone, "mobileno", "mobilenumber", "mobile", "phone");
		assignTextIfMatch(byLabel, answers, b::setAbhaId, "abhaid", "abha");
		assignTextIfMatch(byLabel, answers, b::setVillage, "nameofvillage", "village");
		assignIntegerIfMatch(byLabel, answers, b::setAge, "age");
		LocalDate lmp = readDateIfMatch(byLabel, answers, "lmp", "lastmenstrualperiod", "lmpdate");
		if (lmp != null) {
			b.setLmpDate(lmp);
			b.setEddDate(lmp.plusDays(280));
		}
	}

	private static Map<String, FormQuestion> indexQuestionsBySimplifiedLabel(List<FormQuestion> qs) {
		Map<String, FormQuestion> out = new HashMap<>();
		for (FormQuestion q : qs) {
			out.put(simplify(q.getLabelEn()), q);
		}
		return out;
	}

	private static void assignTextIfMatch(
			Map<String, FormQuestion> byLabel,
			Map<String, Object> answers,
			java.util.function.Consumer<String> setter,
			String... labels) {
		for (String label : labels) {
			FormQuestion q = byLabel.get(label);
			if (q == null) {
				continue;
			}
			Object value = answers.get(q.getCode());
			if (value == null) {
				continue;
			}
			String s = String.valueOf(value).trim();
			if (!s.isEmpty()) {
				setter.accept(s);
				return;
			}
		}
	}

	private static void assignIntegerIfMatch(
			Map<String, FormQuestion> byLabel,
			Map<String, Object> answers,
			java.util.function.Consumer<Integer> setter,
			String... labels) {
		for (String label : labels) {
			FormQuestion q = byLabel.get(label);
			if (q == null) {
				continue;
			}
			Object value = answers.get(q.getCode());
			if (value == null) {
				continue;
			}
			try {
				BigDecimal bd = new BigDecimal(String.valueOf(value));
				setter.accept(bd.intValueExact());
				return;
			}
			catch (ArithmeticException | NumberFormatException ignored) {
				// fall through to next label
			}
		}
	}

	private static LocalDate readDateIfMatch(
			Map<String, FormQuestion> byLabel,
			Map<String, Object> answers,
			String... labels) {
		for (String label : labels) {
			FormQuestion q = byLabel.get(label);
			if (q == null) {
				continue;
			}
			Object value = answers.get(q.getCode());
			LocalDate parsed = parseDateLoose(value);
			if (parsed != null) {
				return parsed;
			}
		}
		return null;
	}

	private static final DateTimeFormatter DATE_DDMMYYYY_SLASH = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final DateTimeFormatter DATE_DDMMYYYY_DASH = DateTimeFormatter.ofPattern("dd-MM-yyyy");

	private static LocalDate parseDateLoose(Object raw) {
		if (raw == null) {
			return null;
		}
		if (raw instanceof LocalDate d) {
			return d;
		}
		String s = String.valueOf(raw).trim();
		if (s.isEmpty()) {
			return null;
		}
		try {
			return LocalDate.parse(s);
		}
		catch (DateTimeParseException ignored) {
			// fall through
		}
		try {
			return LocalDate.parse(s, DATE_DDMMYYYY_SLASH);
		}
		catch (DateTimeParseException ignored) {
			// fall through
		}
		try {
			return LocalDate.parse(s, DATE_DDMMYYYY_DASH);
		}
		catch (DateTimeParseException ignored) {
			return null;
		}
	}

	private static String simplify(String s) {
		if (s == null) {
			return "";
		}
		return s.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
	}

	private static boolean isBlank(String s) {
		return s == null || s.trim().isEmpty();
	}

	// ============================================================================
	// Duplicate detection
	// ============================================================================

	/**
	 * Reject the write if another pregnant woman at the same facility already owns this mobile
	 * phone or ABHA id. {@code excludeBeneficiaryId} is the id to ignore on updates (so a woman
	 * doesn't clash with herself); pass {@code null} on registration.
	 *
	 * <p>Scope is intentionally per-facility (not global) because the same family can move between
	 * facilities and we want each facility's roster to be self-consistent without coupling. Either
	 * argument may be blank — only the populated fields are checked.
	 */
	private void rejectIfDuplicate(Long facilityId, String mobilePhone, String abhaId, Long excludeBeneficiaryId) {
		if (mobilePhone != null && !mobilePhone.trim().isEmpty()) {
			beneficiaryRepository.findFirstByFacilityIdAndMobilePhone(facilityId, mobilePhone.trim())
					.filter(existing -> !existing.getId().equals(excludeBeneficiaryId))
					.ifPresent(existing -> {
						throw new ResponseStatusException(HttpStatus.CONFLICT,
								"A pregnant woman with mobile " + mobilePhone + " is already registered at this facility"
										+ " as '" + existing.getFullName() + "' (id=" + existing.getId()
										+ ", code=" + existing.getCode() + "). Open her record to edit instead of"
										+ " creating a new one.");
					});
		}
		if (abhaId != null && !abhaId.trim().isEmpty()) {
			beneficiaryRepository.findFirstByFacilityIdAndAbhaIdIgnoreCase(facilityId, abhaId.trim())
					.filter(existing -> !existing.getId().equals(excludeBeneficiaryId))
					.ifPresent(existing -> {
						throw new ResponseStatusException(HttpStatus.CONFLICT,
								"A pregnant woman with ABHA id " + abhaId + " is already registered at this facility"
										+ " as '" + existing.getFullName() + "' (id=" + existing.getId()
										+ ", code=" + existing.getCode() + "). Open her record to edit instead of"
										+ " creating a new one.");
					});
		}
	}

	// ============================================================================
	// Authorization helpers
	// ============================================================================

	private PortalUser requireUser(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated.");
		}
		return portalUserRepository.findByUsernameIgnoreCaseWithRole(authentication.getName())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unknown user."));
	}

	private void requireFacilityAccess(PortalUser user, Long facilityId) {
		String roleName = user.getPortalRole() != null ? user.getPortalRole().getRoleName() : null;
		if (PortalRole.ADMIN.equalsIgnoreCase(roleName)) {
			return;
		}
		Set<Long> ids = portalUserFacilityRepository.findActiveByPortalUserIdWithGeo(user.getId()).stream()
				.map(PortalUserFacility::getFacility)
				.filter(Objects::nonNull)
				.map(f -> f.getId())
				.collect(Collectors.toCollection(HashSet::new));
		if (!ids.contains(facilityId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,
					"User '" + user.getUsername() + "' (role=" + roleName + ") is not mapped to facility id "
							+ facilityId + ". Mapped facilities: " + (ids.isEmpty() ? "[none]" : ids)
							+ ". Pick an ashaAssignmentId whose facility is in that list,"
							+ " or log in with an account mapped to facility " + facilityId + ".");
		}
	}

	private FacilityWorkerAssignment requireAshaAssignment(Long ashaAssignmentId) {
		FacilityWorkerAssignment a = assignmentRepository.findById(ashaAssignmentId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ASHA assignment not found."));
		if (a.getRole() != HealthWorkerRole.ASHA) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Assignment " + ashaAssignmentId + " is not an ASHA (role=" + a.getRole() + ").");
		}
		if (a.getStatus() != FacilityAssignmentStatus.ACTIVE) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"ASHA assignment " + ashaAssignmentId + " is not ACTIVE.");
		}
		return a;
	}

	private Beneficiary requireBeneficiary(Long id) {
		return beneficiaryRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Beneficiary not found."));
	}

	private Form requireForm(String code) {
		Form f = formRepository.findByCodeIgnoreCase(code).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Form '" + code + "' not found."));
		if (!f.isActive()) {
			throw new ResponseStatusException(HttpStatus.GONE, "Form '" + code + "' is currently disabled.");
		}
		return f;
	}

	/** Returned {@link Optional#empty()} when there is no current value yet. Helpful for prefill comparisons. */
	@SuppressWarnings("unused")
	private static Optional<String> stringValue(Map<String, Object> answers, String code) {
		Object v = answers.get(code);
		return v == null ? Optional.empty() : Optional.of(String.valueOf(v));
	}
}
