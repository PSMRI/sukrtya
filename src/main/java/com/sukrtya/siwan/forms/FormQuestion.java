package com.sukrtya.siwan.forms;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * One question inside a {@link Form}. Rows for a given form are wholesale replaced on each
 * admin Excel upload, but {@link #code} stays stable (defaults to {@code <FORM_CODE>_<faQid>})
 * so existing JSONB answers in {@code beneficiary_form_response} keep resolving.
 */
@Entity
@Table(name = "form_question")
public class FormQuestion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "form_id", nullable = false)
	private Form form;

	@Column(name = "fa_qid", nullable = false)
	private int faQid;

	@Column(nullable = false, length = 80)
	private String code;

	@Column(name = "label_en", nullable = false, length = 500)
	private String labelEn;

	@Column(name = "label_hi", length = 500)
	private String labelHi;

	@Enumerated(EnumType.STRING)
	@Column(name = "answer_type", nullable = false, length = 20)
	private AnswerType answerType;

	@Column(name = "is_mandatory", nullable = false)
	private boolean mandatory;

	@Column(name = "min_value", length = 50)
	private String minValue;

	@Column(name = "max_value", length = 50)
	private String maxValue;

	@Column(name = "default_value", length = 200)
	private String defaultValue;

	@Column(name = "option_set_code", length = 50)
	private String optionSetCode;

	@Column(name = "skip_value", length = 50)
	private String skipValue;

	@Column(name = "skip_to_qid")
	private Integer skipToQid;

	@Enumerated(EnumType.STRING)
	@Column(name = "computed_kind", length = 30)
	private ComputedKind computedKind;

	@Column(name = "computed_source_code", length = 80)
	private String computedSourceCode;

	@Column(name = "computed_offset_days")
	private Integer computedOffsetDays;

	@Column(nullable = false)
	private int sequence;

	@Column(columnDefinition = "TEXT")
	private String remarks;

	public Long getId() {
		return id;
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}

	public int getFaQid() {
		return faQid;
	}

	public void setFaQid(int faQid) {
		this.faQid = faQid;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLabelEn() {
		return labelEn;
	}

	public void setLabelEn(String labelEn) {
		this.labelEn = labelEn;
	}

	public String getLabelHi() {
		return labelHi;
	}

	public void setLabelHi(String labelHi) {
		this.labelHi = labelHi;
	}

	public AnswerType getAnswerType() {
		return answerType;
	}

	public void setAnswerType(AnswerType answerType) {
		this.answerType = answerType;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public String getMinValue() {
		return minValue;
	}

	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}

	public String getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getOptionSetCode() {
		return optionSetCode;
	}

	public void setOptionSetCode(String optionSetCode) {
		this.optionSetCode = optionSetCode;
	}

	public String getSkipValue() {
		return skipValue;
	}

	public void setSkipValue(String skipValue) {
		this.skipValue = skipValue;
	}

	public Integer getSkipToQid() {
		return skipToQid;
	}

	public void setSkipToQid(Integer skipToQid) {
		this.skipToQid = skipToQid;
	}

	public ComputedKind getComputedKind() {
		return computedKind;
	}

	public void setComputedKind(ComputedKind computedKind) {
		this.computedKind = computedKind;
	}

	public String getComputedSourceCode() {
		return computedSourceCode;
	}

	public void setComputedSourceCode(String computedSourceCode) {
		this.computedSourceCode = computedSourceCode;
	}

	public Integer getComputedOffsetDays() {
		return computedOffsetDays;
	}

	public void setComputedOffsetDays(Integer computedOffsetDays) {
		this.computedOffsetDays = computedOffsetDays;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}
