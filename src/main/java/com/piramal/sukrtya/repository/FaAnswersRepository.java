package com.piramal.sukrtya.repository;

import com.piramal.sukrtya.entity.TblFaAnswers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FaAnswersRepository extends JpaRepository<TblFaAnswers, Long> {
    Optional<TblFaAnswers> findByFaTransactionsIDAndFaQIDAndFormId(String faTransactionsID, Integer faQID, Integer formId);
}

