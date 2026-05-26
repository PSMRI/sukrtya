package com.sukrtya.siwan.forms.dto;

import java.time.Instant;

import com.sukrtya.siwan.beneficiary.ResponseStatus;

/** Summary of a form response's lifecycle for one beneficiary + form pair. */
public record ResponseStatusView(
		boolean exists,
		ResponseStatus status,
		Integer formVersion,
		Instant submittedAt,
		Instant updatedAt) {

	public static ResponseStatusView none() {
		return new ResponseStatusView(false, null, null, null, null);
	}
}
