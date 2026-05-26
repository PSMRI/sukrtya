package com.sukrtya.siwan.beneficiary;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BeneficiaryFormResponseRepository extends JpaRepository<BeneficiaryFormResponse, Long> {

	Optional<BeneficiaryFormResponse> findByBeneficiaryIdAndFormId(Long beneficiaryId, Long formId);

	@Query("""
			select r from BeneficiaryFormResponse r
			join fetch r.form f
			where r.beneficiary.id = :beneficiaryId
			order by f.sequence asc, f.name asc
			""")
	List<BeneficiaryFormResponse> findByBeneficiaryIdOrderedByForm(@Param("beneficiaryId") Long beneficiaryId);

	List<BeneficiaryFormResponse> findByBeneficiaryId(Long beneficiaryId);

	boolean existsByBeneficiaryIdAndFormId(Long beneficiaryId, Long formId);

	long countByBeneficiaryId(Long beneficiaryId);
}
