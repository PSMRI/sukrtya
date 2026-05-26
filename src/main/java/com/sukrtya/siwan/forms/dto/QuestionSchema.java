package com.sukrtya.siwan.forms.dto;

import java.util.List;

import com.sukrtya.siwan.forms.AnswerType;

/** A single question in a dynamic form schema, with everything the frontend needs to render it. */
public record QuestionSchema(
		String code,
		int faQid,
		String labelEn,
		String labelHi,
		AnswerType answerType,
		boolean mandatory,
		String minValue,
		String maxValue,
		String defaultValue,
		String remarks,
		List<QuestionOptionView> options,
		SkipRule skip,
		ComputedSpec computed,
		Object currentValue,
		int sequence) {

	public boolean readOnly() {
		return computed != null;
	}
}
