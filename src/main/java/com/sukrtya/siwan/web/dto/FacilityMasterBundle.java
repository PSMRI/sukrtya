package com.sukrtya.siwan.web.dto;

import java.util.List;

public record FacilityMasterBundle(
		DistrictSummary district,
		BlockSummary block,
		FacilitySummary facility,
		boolean userPrimaryFacilityMapping,
		List<StaffAssignmentRow> staff) {
}
