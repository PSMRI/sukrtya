package com.sukrtya.siwan.web;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sukrtya.siwan.forms.FormCatalogService;
import com.sukrtya.siwan.forms.FormExcelImportService;
import com.sukrtya.siwan.forms.FormImportResult;
import com.sukrtya.siwan.forms.dto.FormDetail;
import com.sukrtya.siwan.forms.dto.FormSummary;
import com.sukrtya.siwan.forms.dto.FormUpsertRequest;
import com.sukrtya.siwan.forms.dto.OptionSetView;

import jakarta.validation.Valid;

/**
 * Admin-only CRUD for the survey catalog + Excel upload for question/option configuration.
 *
 * <p>Endpoints live under {@code /api/admin/forms} and follow the same {@code permitAll} pattern as
 * {@link MasterDataImportController} for now — they should be tightened to {@code ADMIN} before
 * production (see {@link com.sukrtya.siwan.config.SecurityConfig}).
 */
@RestController
@RequestMapping("/api/admin/forms")
public class FormAdminController {

	private final FormCatalogService formCatalogService;
	private final FormExcelImportService formExcelImportService;

	public FormAdminController(
			FormCatalogService formCatalogService,
			FormExcelImportService formExcelImportService) {
		this.formCatalogService = formCatalogService;
		this.formExcelImportService = formExcelImportService;
	}

	@GetMapping
	public List<FormSummary> list() {
		return formCatalogService.listForms();
	}

	@GetMapping("/{code}")
	public FormDetail get(@PathVariable("code") String code) {
		return formCatalogService.getByCode(code);
	}

	@PostMapping
	public FormSummary create(@Valid @RequestBody FormUpsertRequest request) {
		return formCatalogService.create(request);
	}

	@PutMapping("/{code}")
	public FormSummary update(@PathVariable("code") String code, @Valid @RequestBody FormUpsertRequest request) {
		return formCatalogService.update(code, request);
	}

	@PostMapping("/{code}/activate")
	public FormSummary activate(@PathVariable("code") String code) {
		return formCatalogService.setActive(code, true);
	}

	@PostMapping("/{code}/deactivate")
	public FormSummary deactivate(@PathVariable("code") String code) {
		return formCatalogService.setActive(code, false);
	}

	/**
	 * Convenience: same effect as {@code POST /{code}/deactivate}. Kept so the React admin UI can
	 * use a stable {@code DELETE} verb for "remove from active set".
	 */
	@DeleteMapping("/{code}")
	public FormSummary delete(@PathVariable("code") String code) {
		return formCatalogService.setActive(code, false);
	}

	@GetMapping("/option-sets")
	public List<OptionSetView> listOptionSets() {
		return formCatalogService.listOptionSets();
	}

	/**
	 * Upload a workbook with {@code Question Master} (required) and {@code OptionMaster} (optional) sheets.
	 * Questions for every {@code formCode} present are wholesale replaced; option sets are upserted.
	 */
	@PostMapping(value = "/import-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<FormImportResult> importExcel(@RequestParam("file") MultipartFile file) {
		if (file == null || file.isEmpty()) {
			return ResponseEntity.badRequest()
					.body(FormImportResult.error("No file uploaded (use form field name \"file\")."));
		}
		String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "";
		if (!original.toLowerCase().endsWith(".xlsx") && !original.toLowerCase().endsWith(".xls")) {
			return ResponseEntity.badRequest()
					.body(FormImportResult.error("Expected .xlsx or .xls file."));
		}
		try {
			FormImportResult result = formExcelImportService.importFormsWorkbook(file.getInputStream());
			if (!result.success()) {
				return ResponseEntity.badRequest().body(result);
			}
			return ResponseEntity.ok(result);
		}
		catch (IOException e) {
			return ResponseEntity.badRequest()
					.body(FormImportResult.error("Could not read workbook: " + e.getMessage()));
		}
	}
}
