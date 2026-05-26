package com.sukrtya.siwan.forms.dto;

import java.util.List;

import com.sukrtya.siwan.forms.AnswerType;
import com.sukrtya.siwan.forms.ComputedKind;
import com.sukrtya.siwan.forms.FormQuestion;

/**
 * Read-only view of a configured question (used by admin preview and the dynamic form schema).
 *
 * <p>For SINGLE_CHOICE / MULTI_CHOICE questions, {@link #options} is the expanded list of
 * choices resolved from {@link #optionSetCode}. It is {@code null} (not an empty list) for
 * non-choice questions so the JSON shape clearly distinguishes "no options needed" from
 * "options needed but the set has no values configured yet".
 */
public record FormQuestionView(
		Long id,
		int faQid,
		String code,
		String labelEn,
		String labelHi,
		AnswerType answerType,
		boolean mandatory,
		String minValue,
		String maxValue,
		String defaultValue,
		String optionSetCode,
		List<QuestionOptionView> options,
		String skipValue,
		Integer skipToQid,
		ComputedKind computedKind,
		String computedSourceCode,
		Integer computedOffsetDays,
		int sequence,
		String remarks) {

	public static FormQuestionView of(FormQuestion q) {
		return of(q, null);
	}

	public static FormQuestionView of(FormQuestion q, List<QuestionOptionView> options) {
		return new FormQuestionView(
				q.getId(),
				q.getFaQid(),
				q.getCode(),
				q.getLabelEn(),
				q.getLabelHi(),
				q.getAnswerType(),
				q.isMandatory(),
				q.getMinValue(),
				q.getMaxValue(),
				q.getDefaultValue(),
				q.getOptionSetCode(),
				options,
				q.getSkipValue(),
				q.getSkipToQid(),
				q.getComputedKind(),
				q.getComputedSourceCode(),
				q.getComputedOffsetDays(),
				q.getSequence(),
				q.getRemarks());
	}
}
