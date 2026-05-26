package com.sukrtya.siwan.beneficiary;

import java.time.Instant;
import java.time.LocalDate;

import com.sukrtya.siwan.master.Facility;
import com.sukrtya.siwan.master.FacilityWorkerAssignment;

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
 * Pregnant-woman master record. Created when the BASIC form is first submitted under an ASHA.
 * Hot fields (name, mobile, LMP/EDD, village, status) are promoted to typed columns so the
 * "women under this ASHA" listing query stays fast; all other answers live in the JSONB
 * {@code beneficiary_form_response.answers}.
 */
@Entity
@Table(name = "beneficiary")
public class Beneficiary {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "facility_id", nullable = false)
	private Facility facility;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "asha_assignment_id", nullable = false)
	private FacilityWorkerAssignment ashaAssignment;

	@Column(nullable = false, unique = true, length = 40)
	private String code;

	@Column(name = "full_name", nullable = false, length = 200)
	private String fullName;

	@Column(name = "husband_name", length = 200)
	private String husbandName;

	@Column(name = "mobile_phone", length = 20)
	private String mobilePhone;

	@Column(name = "abha_id", length = 50)
	private String abhaId;

	@Column(length = 200)
	private String village;

	private Integer age;

	@Column(name = "lmp_date")
	private LocalDate lmpDate;

	@Column(name = "edd_date")
	private LocalDate eddDate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private BeneficiaryStatus status = BeneficiaryStatus.ACTIVE;

	@Column(name = "created_by")
	private Long createdBy;

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

	public Facility getFacility() {
		return facility;
	}

	public void setFacility(Facility facility) {
		this.facility = facility;
	}

	public FacilityWorkerAssignment getAshaAssignment() {
		return ashaAssignment;
	}

	public void setAshaAssignment(FacilityWorkerAssignment ashaAssignment) {
		this.ashaAssignment = ashaAssignment;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getHusbandName() {
		return husbandName;
	}

	public void setHusbandName(String husbandName) {
		this.husbandName = husbandName;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getAbhaId() {
		return abhaId;
	}

	public void setAbhaId(String abhaId) {
		this.abhaId = abhaId;
	}

	public String getVillage() {
		return village;
	}

	public void setVillage(String village) {
		this.village = village;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public LocalDate getLmpDate() {
		return lmpDate;
	}

	public void setLmpDate(LocalDate lmpDate) {
		this.lmpDate = lmpDate;
	}

	public LocalDate getEddDate() {
		return eddDate;
	}

	public void setEddDate(LocalDate eddDate) {
		this.eddDate = eddDate;
	}

	public BeneficiaryStatus getStatus() {
		return status;
	}

	public void setStatus(BeneficiaryStatus status) {
		this.status = status;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}
}
