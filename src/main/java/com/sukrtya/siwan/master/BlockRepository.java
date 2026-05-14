package com.sukrtya.siwan.master;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockRepository extends JpaRepository<Block, Long> {

	Optional<Block> findByDistrictIdAndNameIgnoreCase(Long districtId, String name);
}
