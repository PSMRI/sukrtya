package com.sukrtya.siwan.beneficiary.dto;

import java.util.Map;

import com.sukrtya.siwan.beneficiary.ResponseStatus;

/**
 * Payload for registering or updating a beneficiary via the BASIC form. The {@code answers} map is
 * keyed by question code (e.g. {@code BASIC_9}, {@code BASIC_16}); hot fields are auto-projected
 * onto the typed beneficiary columns.
 */
public record BeneficiaryUpsertRequest(
		Map<String, Object> answers,
		ResponseStatus status) {

	public ResponseStatus statusOrDefault() {
		return status != null ? status : ResponseStatus.SUBMITTED;
	}
}
