package com.sukrtya.siwan.forms.dto;

import java.util.List;
import java.util.Map;

/**
 * Self-contained dynamic schema for one form, optionally bound to a beneficiary so that
 * computed fields are pre-resolved and existing answers are echoed back for edit flows.
 */
public record FormSchemaResponse(
		FormSummary form,
		List<QuestionSchema> questions,
		Map<String, Object> prefill,
		Long beneficiaryId,
		ResponseStatusView response) {
}
