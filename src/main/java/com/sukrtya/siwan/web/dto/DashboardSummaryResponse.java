package com.sukrtya.siwan.web.dto;

/**
 * Aggregate master-data and coverage metrics for dashboards.
 */
public record DashboardSummaryResponse(
		GeographyTotals geography,
		StaffRoleCounts activeStaffAssignmentsByRole,
		AssignmentStatusTotals facilityWorkerAssignmentsByStatus,
		PortalUserMapping portalUserFacility,
		ChoFacilityCoverage choAtActiveFacilities,
		HealthWorkerTotals healthWorkers) {

	public record GeographyTotals(
			long totalDistricts,
			long activeDistricts,
			long totalBlocks,
			long activeBlocks,
			long totalFacilities,
			long activeFacilities) {
	}

	/** Active {@code facility_worker_assignment} rows per role. */
	public record StaffRoleCounts(long cho, long anm, long ashaFacilitator, long asha) {
	}

	public record AssignmentStatusTotals(long active, long disabled, long unmapped) {
	}

	/**
	 * Portal login ↔ HWC links ({@code portal_user_facility}).
	 * {@code distinctFacilitiesWithActivePortalUser} counts active facilities that have at least one active mapping.
	 * {@code activeFacilitiesWithoutPortalUser} counts active facilities with no active portal mapping (no data-collector login tied to that HWC).
	 */
	public record PortalUserMapping(
			long distinctFacilitiesWithActivePortalUser,
			long activeFacilitiesWithoutPortalUser,
			long activeMappingRows,
			long totalPortalUsers,
			long enabledPortalUsers) {
	}

	/** Among {@code activeFacilities}, how many have an ACTIVE CHO assignment vs not. */
	public record ChoFacilityCoverage(
			long activeFacilitiesWithActiveCho,
			long activeFacilitiesWithoutActiveCho) {
	}

	public record HealthWorkerTotals(long total, long active) {
	}
}
