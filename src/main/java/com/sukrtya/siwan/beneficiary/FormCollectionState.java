package com.sukrtya.siwan.beneficiary;

/**
 * Explains why a form appears empty or filled on beneficiary list/detail screens.
 * Helps the UI distinguish &quot;not started yet&quot; from &quot;form is soft-deleted (inactive)&quot;.
 */
public enum FormCollectionState {

	/** Form is active in the catalog; beneficiary has not opened this form yet. */
	NOT_STARTED,

	/** Form is active; a DRAFT response exists. */
	DRAFT,

	/** Form is active; response was submitted. */
	SUBMITTED,

	/** Form is inactive (soft-deleted); no response was ever saved — hide from collection UI. */
	FORM_INACTIVE,

	/** Form is inactive but historical response data exists (read-only / archive). */
	FORM_INACTIVE_HAS_DATA
}
