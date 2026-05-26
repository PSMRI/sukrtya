package com.sukrtya.siwan.forms;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "option_value")
public class OptionValue {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "option_set_id", nullable = false)
	private OptionSet optionSet;

	@Column(nullable = false, length = 50)
	private String value;

	@Column(name = "label_en", nullable = false, length = 300)
	private String labelEn;

	@Column(name = "label_hi", length = 300)
	private String labelHi;

	@Column(nullable = false)
	private int sequence;

	public Long getId() {
		return id;
	}

	public OptionSet getOptionSet() {
		return optionSet;
	}

	public void setOptionSet(OptionSet optionSet) {
		this.optionSet = optionSet;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
}
