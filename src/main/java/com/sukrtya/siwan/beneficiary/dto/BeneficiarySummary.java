package com.sukrtya.siwan.beneficiary.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import com.sukrtya.siwan.beneficiary.Beneficiary;
import com.sukrtya.siwan.beneficiary.BeneficiaryStatus;

/** Compact view returned by ASHA-scoped beneficiary listings. */
public record BeneficiarySummary(
		Long id,
		String code,
		Long facilityId,
		Long ashaAssignmentId,
		String fullName,
		String husbandName,
		String mobilePhone,
		String abhaId,
		String village,
		Integer age,
		LocalDate lmpDate,
		LocalDate eddDate,
		BeneficiaryStatus status,
		Instant createdAt,
		Instant updatedAt,
		List<FormResponseStatusSummary> formStatus) {

	public static BeneficiarySummary of(Beneficiary b, List<FormResponseStatusSummary> formStatus) {
		return new BeneficiarySummary(
				b.getId(),
				b.getCode(),
				b.getFacility() != null ? b.getFacility().getId() : null,
				b.getAshaAssignment() != null ? b.getAshaAssignment().getId() : null,
				b.getFullName(),
				b.getHusbandName(),
				b.getMobilePhone(),
				b.getAbhaId(),
				b.getVillage(),
				b.getAge(),
				b.getLmpDate(),
				b.getEddDate(),
				b.getStatus(),
				b.getCreatedAt(),
				b.getUpdatedAt(),
				formStatus);
	}
}
