package com.sukrtya.siwan.forms;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionSetRepository extends JpaRepository<OptionSet, Long> {

	Optional<OptionSet> findByCodeIgnoreCase(String code);

	List<OptionSet> findAllByOrderByCodeAsc();
}
