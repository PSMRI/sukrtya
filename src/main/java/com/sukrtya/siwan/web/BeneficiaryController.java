package com.sukrtya.siwan.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sukrtya.siwan.beneficiary.BeneficiaryService;
import com.sukrtya.siwan.beneficiary.dto.BeneficiaryDetail;
import com.sukrtya.siwan.beneficiary.dto.BeneficiaryFormResponseView;
import com.sukrtya.siwan.beneficiary.dto.BeneficiarySummary;
import com.sukrtya.siwan.beneficiary.dto.BeneficiaryUpsertRequest;
import com.sukrtya.siwan.beneficiary.dto.FormSubmitRequest;

/**
 * Pregnant-woman tracker endpoints. The frontend tree is:
 *
 * <pre>
 * CHO login -> facility list (existing) -> ASHA list (existing) -> click ASHA
 *           -> GET  /api/asha/{ashaAssignmentId}/beneficiaries          (list under one ASHA)
 *           -> POST /api/asha/{ashaAssignmentId}/beneficiaries          (register new from BASIC form)
 *           -> GET  /api/beneficiaries/{id}                             (detail with per-form status)
 *           -> PUT  /api/beneficiaries/{id}                             (edit BASIC)
 *           -> POST /api/beneficiaries/{id}/forms/{formCode}            (submit / update ANC1..4)
 *           -> GET  /api/beneficiaries/{id}/forms/{formCode}            (read current response)
 * </pre>
 *
 * <p>All routes require JWT auth (see {@link com.sukrtya.siwan.config.SecurityConfig}). Non-admin
 * users are restricted to facilities they are mapped to via {@code portal_user_facility}.
 */
@RestController
public class BeneficiaryController {

	private final BeneficiaryService beneficiaryService;

	public BeneficiaryController(BeneficiaryService beneficiaryService) {
		this.beneficiaryService = beneficiaryService;
	}

	@GetMapping("/api/asha/{ashaAssignmentId}/beneficiaries")
	public List<BeneficiarySummary> listUnderAsha(
			@PathVariable("ashaAssignmentId") Long ashaAssignmentId,
			Authentication authentication) {
		return beneficiaryService.listUnderAsha(ashaAssignmentId, authentication);
	}

	@PostMapping("/api/asha/{ashaAssignmentId}/beneficiaries")
	public ResponseEntity<BeneficiarySummary> register(
			@PathVariable("ashaAssignmentId") Long ashaAssignmentId,
			@RequestBody BeneficiaryUpsertRequest request,
			Authentication authentication) {
		BeneficiarySummary created = beneficiaryService.register(ashaAssignmentId, request, authentication);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@GetMapping("/api/beneficiaries/{id}")
	public BeneficiaryDetail get(
			@PathVariable("id") Long id,
			Authentication authentication) {
		return beneficiaryService.getDetail(id, authentication);
	}

	@PutMapping("/api/beneficiaries/{id}")
	public BeneficiarySummary updateBasic(
			@PathVariable("id") Long id,
			@RequestBody BeneficiaryUpsertRequest request,
			Authentication authentication) {
		return beneficiaryService.updateBasic(id, request, authentication);
	}

	@GetMapping("/api/beneficiaries/{id}/forms/{formCode}")
	public BeneficiaryFormResponseView getFormResponse(
			@PathVariable("id") Long id,
			@PathVariable("formCode") String formCode,
			Authentication authentication) {
		return beneficiaryService.getFormResponse(id, formCode, authentication);
	}

	@PostMapping("/api/beneficiaries/{id}/forms/{formCode}")
	public BeneficiaryFormResponseView submitFormResponse(
			@PathVariable("id") Long id,
			@PathVariable("formCode") String formCode,
			@RequestBody FormSubmitRequest request,
			Authentication authentication) {
		return beneficiaryService.submitForm(id, formCode, request, authentication);
	}
}
