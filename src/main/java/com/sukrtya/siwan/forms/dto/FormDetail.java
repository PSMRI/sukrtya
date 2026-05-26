package com.sukrtya.siwan.forms.dto;

import java.util.List;

import com.sukrtya.siwan.forms.Form;

/** Form catalog row + its currently configured questions (admin preview). */
public record FormDetail(
		FormSummary form,
		List<FormQuestionView> questions) {

	public static FormDetail of(Form form, List<FormQuestionView> questions) {
		return new FormDetail(FormSummary.of(form, questions.size()), questions);
	}
}
