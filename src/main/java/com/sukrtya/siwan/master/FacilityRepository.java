package com.sukrtya.siwan.master;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityRepository extends JpaRepository<Facility, Long> {

	Optional<Facility> findByBlockIdAndNameIgnoreCase(Long blockId, String name);

	boolean existsByCode(String code);
}
