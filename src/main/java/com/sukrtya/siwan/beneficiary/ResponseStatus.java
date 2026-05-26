package com.sukrtya.siwan.beneficiary;

/** Submission state of a single form response. Mirrors the DB check on {@code beneficiary_form_response.status}. */
public enum ResponseStatus {
	DRAFT,
	SUBMITTED
}
