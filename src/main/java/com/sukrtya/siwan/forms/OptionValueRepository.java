package com.sukrtya.siwan.forms;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface OptionValueRepository extends JpaRepository<OptionValue, Long> {

	List<OptionValue> findByOptionSetIdOrderBySequenceAscValueAsc(Long optionSetId);

	@Transactional
	long deleteByOptionSetId(Long optionSetId);
}
