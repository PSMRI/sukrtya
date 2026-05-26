package com.sukrtya.siwan.forms;

/**
 * Optional marker for computed (auto-filled, read-only) form fields. Parsed from the Excel
 * {@code Remarks} column (e.g. {@code "LMP Date + 280 Days"} -> {@link #DATE_OFFSET}).
 */
public enum ComputedKind {
	DATE_OFFSET
}
