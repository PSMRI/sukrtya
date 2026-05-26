package com.sukrtya.siwan.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sukrtya.siwan.forms.FormSchemaService;
import com.sukrtya.siwan.forms.dto.FormSchemaResponse;

/**
 * Read-only schema endpoint that the data-collection frontend hits when opening any form. Returns
 * everything needed to render: questions in order, options expanded, skip rules + computed-field
 * formulas, plus pre-resolved values + existing answers when a beneficiary context is supplied.
 *
 * <p>Lives under {@code /api/forms/**} so it sits behind JWT auth (see
 * {@link com.sukrtya.siwan.config.SecurityConfig}).
 */
@RestController
@RequestMapping("/api/forms")
public class FormSchemaController {

	private final FormSchemaService formSchemaService;

	public FormSchemaController(FormSchemaService formSchemaService) {
		this.formSchemaService = formSchemaService;
	}

	@GetMapping("/{code}/schema")
	public FormSchemaResponse schema(
			@PathVariable("code") String code,
			@RequestParam(value = "beneficiaryId", required = false) Long beneficiaryId) {
		return formSchemaService.buildSchema(code, beneficiaryId);
	}
}
