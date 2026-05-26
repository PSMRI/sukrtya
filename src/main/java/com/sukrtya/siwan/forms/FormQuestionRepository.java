package com.sukrtya.siwan.forms;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface FormQuestionRepository extends JpaRepository<FormQuestion, Long> {

	List<FormQuestion> findByFormIdOrderBySequenceAscFaQidAsc(Long formId);

	@Transactional
	long deleteByFormId(Long formId);
}
