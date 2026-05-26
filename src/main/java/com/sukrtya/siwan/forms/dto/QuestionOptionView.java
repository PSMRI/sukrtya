package com.sukrtya.siwan.forms.dto;

import com.sukrtya.siwan.forms.OptionValue;

/** Single dropdown / checkbox option as exposed in a form schema. */
public record QuestionOptionView(String value, String labelEn, String labelHi, int sequence) {

	public static QuestionOptionView of(OptionValue ov) {
		return new QuestionOptionView(ov.getValue(), ov.getLabelEn(), ov.getLabelHi(), ov.getSequence());
	}
}
