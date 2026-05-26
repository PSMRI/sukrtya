package com.sukrtya.siwan.beneficiary.dto;

import java.util.Map;

import com.sukrtya.siwan.beneficiary.ResponseStatus;

/**
 * Payload for {@code POST /api/beneficiaries/{id}/forms/{formCode}}. Used by ANC1..4 (and any
 * future form) — BASIC has its own register / update endpoints because it also mutates the
 * beneficiary's typed columns.
 */
public record FormSubmitRequest(
		Map<String, Object> answers,
		ResponseStatus status) {

	public ResponseStatus statusOrDefault() {
		return status != null ? status : ResponseStatus.SUBMITTED;
	}
}
