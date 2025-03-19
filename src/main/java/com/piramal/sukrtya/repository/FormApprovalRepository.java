package com.piramal.sukrtya.repository;

import com.piramal.sukrtya.entity.TblFormTransactions;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FormApprovalRepository extends JpaRepository<TblFormTransactions, Long> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE tblformtransactions  SET approvedby = :userId, approveddate = TO_CHAR(CURRENT_TIMESTAMP, 'YYYY-MM-DD HH24:MI:SS'), F7 = :approvalStatus WHERE formid = :formId AND transactionid = :transactionId", nativeQuery = true)
    int updateFormApproval(@Param("userId") Long userId,
                           @Param("approvalStatus") String approvalStatus,
                           @Param("formId") Integer formId,
                           @Param("transactionId") String transactionId);
}