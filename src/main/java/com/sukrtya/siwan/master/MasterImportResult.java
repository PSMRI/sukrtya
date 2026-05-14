package com.sukrtya.siwan.master;

import java.util.List;

public record MasterImportResult(boolean success, int rowsImported, int rowsSkipped, List<String> messages) {

	public static MasterImportResult error(String message) {
		return new MasterImportResult(false, 0, 0, List.of(message));
	}
}
