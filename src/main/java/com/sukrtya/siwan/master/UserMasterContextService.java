package com.sukrtya.siwan.master;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sukrtya.siwan.portal.PortalRole;
import com.sukrtya.siwan.portal.PortalUser;
import com.sukrtya.siwan.portal.PortalUserFacility;
import com.sukrtya.siwan.portal.PortalUserFacilityRepository;
import com.sukrtya.siwan.web.dto.BlockSummary;
import com.sukrtya.siwan.web.dto.DistrictSummary;
import com.sukrtya.siwan.web.dto.FacilityMasterBundle;
import com.sukrtya.siwan.web.dto.FacilitySummary;
import com.sukrtya.siwan.web.dto.HealthWorkerSummary;
import com.sukrtya.siwan.web.dto.MasterContextResponse;
import com.sukrtya.siwan.web.dto.StaffAssignmentRow;

@Service
public class UserMasterContextService {

	private final FacilityRepository facilityRepository;
	private final PortalUserFacilityRepository portalUserFacilityRepository;
	private final FacilityWorkerAssignmentRepository assignmentRepository;

	public UserMasterContextService(
			FacilityRepository facilityRepository,
			PortalUserFacilityRepository portalUserFacilityRepository,
			FacilityWorkerAssignmentRepository assignmentRepository) {
		this.facilityRepository = facilityRepository;
		this.portalUserFacilityRepository = portalUserFacilityRepository;
		this.assignmentRepository = assignmentRepository;
	}

	@Transactional(readOnly = true)
	public MasterContextResponse buildFor(PortalUser portalUser) {
		String roleName = portalUser.getPortalRole().getRoleName();
		boolean admin = PortalRole.ADMIN.equalsIgnoreCase(roleName);

		List<Facility> facilities;
		Map<Long, Boolean> primaryByFacilityId = new HashMap<>();

		if (admin) {
			facilities = new ArrayList<>(facilityRepository.findAllWithBlockAndDistrict());
		}
		else {
			List<PortalUserFacility> mappings = portalUserFacilityRepository.findActiveByPortalUserIdWithGeo(portalUser.getId());
			LinkedHashMap<Long, Facility> unique = new LinkedHashMap<>();
			for (PortalUserFacility puf : mappings) {
				Facility f = puf.getFacility();
				unique.putIfAbsent(f.getId(), f);
				if (puf.isPrimary()) {
					primaryByFacilityId.put(f.getId(), true);
				}
			}
			facilities = new ArrayList<>(unique.values());
		}

		List<Long> facilityIds = facilities.stream().map(Facility::getId).toList();
		List<FacilityWorkerAssignment> assignments = facilityIds.isEmpty()
				? List.of()
				: assignmentRepository.findActiveByFacilityIdsWithDetails(facilityIds, FacilityAssignmentStatus.ACTIVE);

		Map<Long, List<FacilityWorkerAssignment>> byFacilityId = assignments.stream()
				.collect(Collectors.groupingBy(a -> a.getFacility().getId()));

		List<FacilityMasterBundle> bundles = new ArrayList<>();
		for (Facility facility : facilities) {
			Block block = facility.getBlock();
			District district = block.getDistrict();
			boolean mappingPrimary = !admin && primaryByFacilityId.getOrDefault(facility.getId(), false);
			List<StaffAssignmentRow> staff = mapStaff(byFacilityId.getOrDefault(facility.getId(), List.of()));
			bundles.add(new FacilityMasterBundle(
					toDistrictSummary(district),
					toBlockSummary(block),
					toFacilitySummary(facility),
					mappingPrimary,
					staff));
		}

		bundles.sort(Comparator
				.comparing((FacilityMasterBundle b) -> b.district().name(), String.CASE_INSENSITIVE_ORDER)
				.thenComparing(b -> b.block().name(), String.CASE_INSENSITIVE_ORDER)
				.thenComparing(b -> b.facility().name(), String.CASE_INSENSITIVE_ORDER));

		String scope = admin ? MasterContextResponse.SCOPE_ALL : MasterContextResponse.SCOPE_MAPPED_FACILITIES;
		return new MasterContextResponse(
				scope,
				portalUser.getId(),
				portalUser.getUsername(),
				portalUser.getDisplayName(),
				roleName,
				bundles);
	}

	private static DistrictSummary toDistrictSummary(District d) {
		return new DistrictSummary(d.getId(), d.getName(), d.getCode(), d.isActive());
	}

	private static BlockSummary toBlockSummary(Block b) {
		return new BlockSummary(b.getId(), b.getName(), b.getCode(), b.isActive(), b.getDistrict().getId());
	}

	private static FacilitySummary toFacilitySummary(Facility f) {
		return new FacilitySummary(f.getId(), f.getName(), f.getCode(), f.getFacilityType(), f.isActive(), f.getBlock().getId());
	}

	private static List<StaffAssignmentRow> mapStaff(List<FacilityWorkerAssignment> rows) {
		List<FacilityWorkerAssignment> sorted = new ArrayList<>(rows);
		sorted.sort(ASSIGNMENT_ORDER);
		return sorted.stream().map(UserMasterContextService::toStaffRow).toList();
	}

	private static final Comparator<FacilityWorkerAssignment> ASSIGNMENT_ORDER = Comparator
			.comparing(FacilityWorkerAssignment::getRole)
			.thenComparing(a -> a.getHealthWorker().getFullName(), String.CASE_INSENSITIVE_ORDER);

	private static StaffAssignmentRow toStaffRow(FacilityWorkerAssignment a) {
		HealthWorker w = a.getHealthWorker();
		Long supId = a.getSupervisorAssignment() != null ? a.getSupervisorAssignment().getId() : null;
		return new StaffAssignmentRow(
				a.getId(),
				a.getRole().name(),
				a.getStatus().name(),
				a.getStaffType(),
				new HealthWorkerSummary(w.getId(), w.getFullName(), w.getMobilePhone(), w.isActive()),
				supId);
	}
}
