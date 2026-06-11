package com.sukrtya.siwan.forms;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FormRepository extends JpaRepository<Form, Long> {

	Optional<Form> findByCodeIgnoreCase(String code);

	boolean existsByCodeIgnoreCase(String code);

	List<Form> findAllByOrderBySequenceAscNameAsc();

	List<Form> findAllByActiveTrueOrderBySequenceAscNameAsc();

	List<Form> findByPrerequisiteCodeIgnoreCase(String prerequisiteCode);
}
