package com.sukrtya.siwan.beneficiary;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {

	List<Beneficiary> findByAshaAssignmentIdOrderByCreatedAtDesc(Long ashaAssignmentId);

	List<Beneficiary> findByFacilityIdOrderByCreatedAtDesc(Long facilityId);

	Optional<Beneficiary> findByCodeIgnoreCase(String code);

	boolean existsByCode(String code);

	/**
	 * Duplicate detection: another pregnant woman at the same facility with this mobile or ABHA.
	 * Empty optional means "no clash, OK to write". Always pass a non-null facilityId; either
	 * mobilePhone or abhaId may be null (caller skips the lookup when the field is missing).
	 */
	Optional<Beneficiary> findFirstByFacilityIdAndMobilePhone(Long facilityId, String mobilePhone);

	Optional<Beneficiary> findFirstByFacilityIdAndAbhaIdIgnoreCase(Long facilityId, String abhaId);

	long countByAshaAssignmentId(Long ashaAssignmentId);

	long countByFacilityId(Long facilityId);
}
