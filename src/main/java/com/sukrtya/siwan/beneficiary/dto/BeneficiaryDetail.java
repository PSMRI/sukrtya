package com.sukrtya.siwan.beneficiary.dto;

import java.util.List;
import java.util.Map;

/**
 * Full beneficiary view returned by {@code GET /api/beneficiaries/{id}}. Includes the woman's
 * record, the per-form status grid for her tracker (BASIC ✓, ANC1 due, ...), and the most
 * recently captured BASIC answers (JSONB pulled straight out of the response row).
 */
public record BeneficiaryDetail(
		BeneficiarySummary beneficiary,
		Map<String, Object> basicAnswers,
		List<FormResponseStatusSummary> forms) {
}
