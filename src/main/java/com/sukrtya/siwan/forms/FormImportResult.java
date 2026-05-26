package com.sukrtya.siwan.forms;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Outcome of an admin Excel upload that configures forms + option sets. Mirrors the shape of
 * {@link com.sukrtya.siwan.master.MasterImportResult} so the existing import UI patterns apply.
 */
public record FormImportResult(
		boolean success,
		int questionsImported,
		int questionsSkipped,
		int optionsImported,
		Map<String, Integer> questionsByForm,
		List<String> messages) {

	public static FormImportResult error(String message) {
		List<String> msgs = new ArrayList<>();
		msgs.add(message);
		return new FormImportResult(false, 0, 0, 0, new LinkedHashMap<>(), msgs);
	}
}
