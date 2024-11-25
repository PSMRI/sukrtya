package com.piramal.sukrtya.repository;

import com.piramal.sukrtya.entity.TblFormTransactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FormSubmissionRepository extends JpaRepository<TblFormTransactions, Long> {
    Optional<TblFormTransactions> findByTransactionId(String transactionId);
}

