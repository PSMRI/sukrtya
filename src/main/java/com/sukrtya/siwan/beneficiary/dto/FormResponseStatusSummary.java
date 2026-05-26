package com.sukrtya.siwan.beneficiary.dto;

import java.time.Instant;

import com.sukrtya.siwan.beneficiary.ResponseStatus;

/** Per-form status block for a beneficiary detail / list view. */
public record FormResponseStatusSummary(
		String formCode,
		String formName,
		int formSequence,
		String prerequisiteCode,
		boolean exists,
		ResponseStatus status,
		Integer formVersion,
		Instant submittedAt,
		Instant updatedAt) {
}
