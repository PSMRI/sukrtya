package com.sukrtya.siwan.master;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sukrtya.siwan.portal.PortalUserFacilityRepository;
import com.sukrtya.siwan.portal.PortalUserRepository;
import com.sukrtya.siwan.web.dto.DashboardSummaryResponse;
import com.sukrtya.siwan.web.dto.DashboardSummaryResponse.AssignmentStatusTotals;
import com.sukrtya.siwan.web.dto.DashboardSummaryResponse.ChoFacilityCoverage;
import com.sukrtya.siwan.web.dto.DashboardSummaryResponse.GeographyTotals;
import com.sukrtya.siwan.web.dto.DashboardSummaryResponse.HealthWorkerTotals;
import com.sukrtya.siwan.web.dto.DashboardSummaryResponse.PortalUserMapping;
import com.sukrtya.siwan.web.dto.DashboardSummaryResponse.StaffRoleCounts;

@Service
public class DashboardStatsService {

	private final DistrictRepository districtRepository;
	private final BlockRepository blockRepository;
	private final FacilityRepository facilityRepository;
	private final FacilityWorkerAssignmentRepository assignmentRepository;
	private final PortalUserFacilityRepository portalUserFacilityRepository;
	private final PortalUserRepository portalUserRepository;
	private final HealthWorkerRepository healthWorkerRepository;

	public DashboardStatsService(
			DistrictRepository districtRepository,
			BlockRepository blockRepository,
			FacilityRepository facilityRepository,
			FacilityWorkerAssignmentRepository assignmentRepository,
			PortalUserFacilityRepository portalUserFacilityRepository,
			PortalUserRepository portalUserRepository,
			HealthWorkerRepository healthWorkerRepository) {
		this.districtRepository = districtRepository;
		this.blockRepository = blockRepository;
		this.facilityRepository = facilityRepository;
		this.assignmentRepository = assignmentRepository;
		this.portalUserFacilityRepository = portalUserFacilityRepository;
		this.portalUserRepository = portalUserRepository;
		this.healthWorkerRepository = healthWorkerRepository;
	}

	@Transactional(readOnly = true)
	public DashboardSummaryResponse buildSummary() {
		GeographyTotals geography = new GeographyTotals(
				districtRepository.count(),
				districtRepository.countByActiveTrue(),
				blockRepository.count(),
				blockRepository.countByActiveTrue(),
				facilityRepository.count(),
				facilityRepository.countByActiveTrue());

		StaffRoleCounts roles = new StaffRoleCounts(
				assignmentRepository.countByRoleAndStatus(HealthWorkerRole.CHO, FacilityAssignmentStatus.ACTIVE),
				assignmentRepository.countByRoleAndStatus(HealthWorkerRole.ANM, FacilityAssignmentStatus.ACTIVE),
				assignmentRepository.countByRoleAndStatus(HealthWorkerRole.ASHA_FACILITATOR, FacilityAssignmentStatus.ACTIVE),
				assignmentRepository.countByRoleAndStatus(HealthWorkerRole.ASHA, FacilityAssignmentStatus.ACTIVE));

		AssignmentStatusTotals statuses = new AssignmentStatusTotals(
				assignmentRepository.countByStatus(FacilityAssignmentStatus.ACTIVE),
				assignmentRepository.countByStatus(FacilityAssignmentStatus.DISABLED),
				assignmentRepository.countByStatus(FacilityAssignmentStatus.UNMAPPED));

		long mappedFacilityCount = portalUserFacilityRepository.countDistinctFacilitiesWithActivePortalMapping();
		long withoutPortal = portalUserFacilityRepository.countActiveFacilitiesWithNoActivePortalMapping();

		PortalUserMapping portal = new PortalUserMapping(
				mappedFacilityCount,
				withoutPortal,
				portalUserFacilityRepository.countActivePortalUserFacilityRows(),
				portalUserRepository.count(),
				portalUserRepository.countByAccountEnabledTrue());

		ChoFacilityCoverage cho = new ChoFacilityCoverage(
				facilityRepository.countActiveFacilitiesWithActiveCho(HealthWorkerRole.CHO, FacilityAssignmentStatus.ACTIVE),
				facilityRepository.countActiveFacilitiesWithoutActiveCho(HealthWorkerRole.CHO, FacilityAssignmentStatus.ACTIVE));

		HealthWorkerTotals workers = new HealthWorkerTotals(
				healthWorkerRepository.count(),
				healthWorkerRepository.countByActiveTrue());

		return new DashboardSummaryResponse(geography, roles, statuses, portal, cho, workers);
	}
}
