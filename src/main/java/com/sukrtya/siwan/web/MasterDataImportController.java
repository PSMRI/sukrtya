package com.sukrtya.siwan.web;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sukrtya.siwan.master.MasterExcelImportService;
import com.sukrtya.siwan.master.MasterImportResult;

@RestController
@RequestMapping("/api/admin/masters")
public class MasterDataImportController {

	private final MasterExcelImportService importService;

	public MasterDataImportController(MasterExcelImportService importService) {
		this.importService = importService;
	}

	/**
	 * Upload Excel (.xlsx or .xls) with columns: District, Block, HWC Facility,
	 * CHO Name/Mobile, ANM Name/Mobile, ASHA Facilitator + Mobile, Asha Name + Mobile (see template).
	 */
	@PostMapping(value = "/import-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<MasterImportResult> importExcel(@RequestParam("file") MultipartFile file) {
		if (file == null || file.isEmpty()) {
			return ResponseEntity.badRequest().body(MasterImportResult.error("No file uploaded (use form field name \"file\")."));
		}
		String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "";
		if (!original.toLowerCase().endsWith(".xlsx") && !original.toLowerCase().endsWith(".xls")) {
			return ResponseEntity.badRequest().body(MasterImportResult.error("Expected .xlsx or .xls file."));
		}
		try {
			MasterImportResult result = importService.importMasterWorkbook(file.getInputStream());
			if (!result.success()) {
				return ResponseEntity.badRequest().body(result);
			}
			return ResponseEntity.ok(result);
		}
		catch (IOException e) {
			return ResponseEntity.badRequest().body(MasterImportResult.error("Could not read workbook: " + e.getMessage()));
		}
	}
}
