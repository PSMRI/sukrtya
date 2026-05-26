package com.sukrtya.siwan.forms.dto;

/**
 * Skip-logic rule attached to a single question. If the user selects {@link #ifValue} the frontend
 * should jump to {@link #toQuestionCode} (resolved server-side from the row's {@code skip_to_qid}).
 */
public record SkipRule(String ifValue, Integer toQid, String toQuestionCode) {
}
