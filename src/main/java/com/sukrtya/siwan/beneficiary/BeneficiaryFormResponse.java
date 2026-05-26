package com.sukrtya.siwan.beneficiary;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.sukrtya.siwan.forms.Form;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

/**
 * Single table for every form response (BASIC, ANC1..4, future PNC/DELIVERY/...).
 * Answers are stored as a JSONB map keyed by {@code form_question.code} so adding a new
 * form requires zero schema changes.
 */
@Entity
@Table(name = "beneficiary_form_response")
public class BeneficiaryFormResponse {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "beneficiary_id", nullable = false)
	private Beneficiary beneficiary;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "form_id", nullable = false)
	private Form form;

	@Column(name = "form_version", nullable = false)
	private int formVersion;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ResponseStatus status = ResponseStatus.DRAFT;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "answers", nullable = false, columnDefinition = "jsonb")
	private Map<String, Object> answers = new HashMap<>();

	@Column(name = "submitted_by_id")
	private Long submittedById;

	@Column(name = "submitted_at")
	private Instant submittedAt;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	@PrePersist
	void prePersist() {
		Instant now = Instant.now();
		if (createdAt == null) {
			createdAt = now;
		}
		updatedAt = now;
	}

	@PreUpdate
	void preUpdate() {
		updatedAt = Instant.now();
	}

	public Long getId() {
		return id;
	}

	public Beneficiary getBeneficiary() {
		return beneficiary;
	}

	public void setBeneficiary(Beneficiary beneficiary) {
		this.beneficiary = beneficiary;
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}

	public int getFormVersion() {
		return formVersion;
	}

	public void setFormVersion(int formVersion) {
		this.formVersion = formVersion;
	}

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

	public Map<String, Object> getAnswers() {
		return answers;
	}

	public void setAnswers(Map<String, Object> answers) {
		this.answers = (answers != null) ? answers : new HashMap<>();
	}

	public Long getSubmittedById() {
		return submittedById;
	}

	public void setSubmittedById(Long submittedById) {
		this.submittedById = submittedById;
	}

	public Instant getSubmittedAt() {
		return submittedAt;
	}

	public void setSubmittedAt(Instant submittedAt) {
		this.submittedAt = submittedAt;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}
}
