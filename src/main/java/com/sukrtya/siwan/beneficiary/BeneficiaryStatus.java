package com.sukrtya.siwan.beneficiary;

/** Lifecycle of a pregnant-woman record. Mirrors the DB check on {@code beneficiary.status}. */
public enum BeneficiaryStatus {
	ACTIVE,
	DELIVERED,
	LOST_TO_FOLLOWUP,
	INVALID
}
