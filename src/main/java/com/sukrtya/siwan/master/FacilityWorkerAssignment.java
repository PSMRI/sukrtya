package com.sukrtya.siwan.master;

import java.time.Instant;

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

@Entity
@Table(name = "facility_worker_assignment")
public class FacilityWorkerAssignment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "facility_id", nullable = false)
	private Facility facility;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "health_worker_id", nullable = false)
	private HealthWorker healthWorker;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false, length = 40)
	private HealthWorkerRole role;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 40)
	private FacilityAssignmentStatus status = FacilityAssignmentStatus.ACTIVE;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "supervisor_assignment_id")
	private FacilityWorkerAssignment supervisorAssignment;

	/** Label from the Excel header for this slot (CHO Name, ANM Name, etc.). DB column: staff_type. */
	@Column(name = "staff_type", length = 200)
	private String staffType;

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

	public HealthWorker getHealthWorker() {
		return healthWorker;
	}

	public void setHealthWorker(HealthWorker healthWorker) {
		this.healthWorker = healthWorker;
	}

	public HealthWorkerRole getRole() {
		return role;
	}

	public void setRole(HealthWorkerRole role) {
		this.role = role;
	}

	public FacilityAssignmentStatus getStatus() {
		return status;
	}

	public void setStatus(FacilityAssignmentStatus status) {
		this.status = status;
	}

	public FacilityWorkerAssignment getSupervisorAssignment() {
		return supervisorAssignment;
	}

	public void setSupervisorAssignment(FacilityWorkerAssignment supervisorAssignment) {
		this.supervisorAssignment = supervisorAssignment;
	}

	public String getStaffType() {
		return staffType;
	}

	public void setStaffType(String staffType) {
		this.staffType = staffType;
	}
}
