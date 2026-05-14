package com.sukrtya.siwan.master;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FacilityWorkerAssignmentRepository extends JpaRepository<FacilityWorkerAssignment, Long> {

	@Query("""
			select f from FacilityWorkerAssignment f
			where f.facility.id = :facilityId and f.role = :role and f.status = :activeStatus
			""")
	Optional<FacilityWorkerAssignment> findActiveSingletonRole(
			@Param("facilityId") Long facilityId,
			@Param("role") HealthWorkerRole role,
			@Param("activeStatus") FacilityAssignmentStatus activeStatus);

	@Query("""
			select count(f) from FacilityWorkerAssignment f
			where f.facility.id = :facilityId and f.healthWorker.id = :workerId
			  and f.role = :role and f.status = :activeStatus
			""")
	long countActiveAssignment(
			@Param("facilityId") Long facilityId,
			@Param("workerId") Long workerId,
			@Param("role") HealthWorkerRole role,
			@Param("activeStatus") FacilityAssignmentStatus activeStatus);

	@Query("""
			select f from FacilityWorkerAssignment f
			where f.facility.id = :facilityId and f.healthWorker.id = :workerId
			  and f.role = :role and f.status = :activeStatus
			""")
	Optional<FacilityWorkerAssignment> findActiveAssignment(
			@Param("facilityId") Long facilityId,
			@Param("workerId") Long workerId,
			@Param("role") HealthWorkerRole role,
			@Param("activeStatus") FacilityAssignmentStatus activeStatus);
}
