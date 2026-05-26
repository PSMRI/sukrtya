package com.sukrtya.siwan.forms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Payload for creating or updating a {@link com.sukrtya.siwan.forms.Form} catalog row. */
public record FormUpsertRequest(
		@NotBlank @Size(max = 50) String code,
		@NotBlank @Size(max = 200) String name,
		Integer sequence,
		@Size(max = 50) String prerequisiteCode,
		String description,
		Boolean active) {
}
