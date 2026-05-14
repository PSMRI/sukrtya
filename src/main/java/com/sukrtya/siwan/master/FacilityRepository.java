package com.sukrtya.siwan.master;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FacilityRepository extends JpaRepository<Facility, Long> {

	Optional<Facility> findByBlockIdAndNameIgnoreCase(Long blockId, String name);

	boolean existsByCode(String code);

	@Query("""
			select distinct f from Facility f
			join fetch f.block b
			join fetch b.district d
			""")
	List<Facility> findAllWithBlockAndDistrict();
}
