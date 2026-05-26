package com.sukrtya.siwan.forms.dto;

import com.sukrtya.siwan.forms.ComputedKind;

/**
 * Read-only computed field metadata for the frontend. {@code resolvedValue} is pre-computed when a
 * beneficiary context is available (e.g. EDD = beneficiary.lmp_date + 280); otherwise null and the
 * client should leave the field blank until the source is filled in.
 */
public record ComputedSpec(
		ComputedKind kind,
		String sourceQuestionCode,
		Integer offsetDays,
		String resolvedValue) {
}
