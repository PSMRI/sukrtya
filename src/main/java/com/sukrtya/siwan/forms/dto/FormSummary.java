package com.sukrtya.siwan.forms.dto;

import java.time.Instant;

import com.sukrtya.siwan.forms.Form;

/** Compact view of a {@link Form} catalog row for admin listings. */
public record FormSummary(
		Long id,
		String code,
		String name,
		int sequence,
		String prerequisiteCode,
		String description,
		boolean active,
		int version,
		long questionCount,
		Instant updatedAt) {

	public static FormSummary of(Form form, long questionCount) {
		return new FormSummary(
				form.getId(),
				form.getCode(),
				form.getName(),
				form.getSequence(),
				form.getPrerequisiteCode(),
				form.getDescription(),
				form.isActive(),
				form.getVersion(),
				questionCount,
				form.getUpdatedAt());
	}
}
