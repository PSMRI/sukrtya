package com.sukrtya.siwan.master;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FacilityRepository extends JpaRepository<Facility, Long> {

	Optional<Facility> findByBlockIdAndNameIgnoreCase(Long blockId, String name);

	boolean existsByCode(String code);

	long count();

	long countByActiveTrue();

	@Query("""
			select count(distinct f.id) from Facility f
			where f.active = true
			and exists (
				select 1 from FacilityWorkerAssignment a
				where a.facility.id = f.id and a.role = :choRole and a.status = :activeStatus)
			""")
	long countActiveFacilitiesWithActiveCho(
			@Param("choRole") HealthWorkerRole choRole,
			@Param("activeStatus") FacilityAssignmentStatus activeStatus);

	@Query("""
			select count(distinct f.id) from Facility f
			where f.active = true
			and not exists (
				select 1 from FacilityWorkerAssignment a
				where a.facility.id = f.id and a.role = :choRole and a.status = :activeStatus)
			""")
	long countActiveFacilitiesWithoutActiveCho(
			@Param("choRole") HealthWorkerRole choRole,
			@Param("activeStatus") FacilityAssignmentStatus activeStatus);

	@Query("""
			select distinct f from Facility f
			join fetch f.block b
			join fetch b.district d
			""")
	List<Facility> findAllWithBlockAndDistrict();
}
