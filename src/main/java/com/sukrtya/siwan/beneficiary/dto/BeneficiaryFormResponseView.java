package com.sukrtya.siwan.beneficiary.dto;

import java.time.Instant;
import java.util.Map;

import com.sukrtya.siwan.beneficiary.BeneficiaryFormResponse;
import com.sukrtya.siwan.beneficiary.ResponseStatus;

/** Server-side state of a single (beneficiary, form) response, for read / write echoes. */
public record BeneficiaryFormResponseView(
		Long id,
		Long beneficiaryId,
		String formCode,
		String formName,
		int formVersion,
		ResponseStatus status,
		Map<String, Object> answers,
		Instant submittedAt,
		Instant updatedAt) {

	public static BeneficiaryFormResponseView of(BeneficiaryFormResponse r) {
		return new BeneficiaryFormResponseView(
				r.getId(),
				r.getBeneficiary().getId(),
				r.getForm().getCode(),
				r.getForm().getName(),
				r.getFormVersion(),
				r.getStatus(),
				r.getAnswers(),
				r.getSubmittedAt(),
				r.getUpdatedAt());
	}
}
