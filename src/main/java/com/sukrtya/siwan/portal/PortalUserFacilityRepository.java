package com.sukrtya.siwan.portal;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PortalUserFacilityRepository extends JpaRepository<PortalUserFacility, Long> {

	Optional<PortalUserFacility> findByPortalUser_IdAndFacility_Id(Long portalUserId, Long facilityId);

	long countByPortalUser_IdAndActiveTrueAndPrimaryTrue(Long portalUserId);
}
