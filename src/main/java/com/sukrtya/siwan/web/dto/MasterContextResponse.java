package com.sukrtya.siwan.web.dto;

import java.util.List;

public record MasterContextResponse(
		String scope,
		long userId,
		String username,
		String displayName,
		String role,
		List<FacilityMasterBundle> facilities) {

	public static final String SCOPE_ALL = "ALL";
	public static final String SCOPE_MAPPED_FACILITIES = "MAPPED_FACILITIES";
}
