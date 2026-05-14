package com.sukrtya.siwan.portal;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PortalUserFacilityRepository extends JpaRepository<PortalUserFacility, Long> {

	Optional<PortalUserFacility> findByPortalUser_IdAndFacility_Id(Long portalUserId, Long facilityId);

	long countByPortalUser_IdAndActiveTrueAndPrimaryTrue(Long portalUserId);

	@Query("""
			select puf from PortalUserFacility puf
			join fetch puf.facility f
			join fetch f.block b
			join fetch b.district d
			where puf.portalUser.id = :portalUserId and puf.active = true
			""")
	List<PortalUserFacility> findActiveByPortalUserIdWithGeo(@Param("portalUserId") Long portalUserId);
}
