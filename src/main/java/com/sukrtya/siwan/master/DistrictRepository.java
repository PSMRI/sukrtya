package com.sukrtya.siwan.master;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DistrictRepository extends JpaRepository<District, Long> {

	Optional<District> findFirstByNameIgnoreCase(String name);
}
