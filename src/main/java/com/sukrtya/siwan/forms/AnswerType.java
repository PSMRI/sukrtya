package com.sukrtya.siwan.forms;

/** Allowed values for {@code form_question.answer_type}. Mirrors the DB check constraint in V5. */
public enum AnswerType {
	TEXT,
	NUMERIC,
	DATE,
	SINGLE_CHOICE,
	MULTI_CHOICE
}
