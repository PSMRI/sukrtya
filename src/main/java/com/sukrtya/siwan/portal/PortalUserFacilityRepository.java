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
			select count(distinct puf.facility.id) from PortalUserFacility puf
			where puf.active = true
			""")
	long countDistinctFacilitiesWithActivePortalMapping();

	@Query("""
			select count(distinct f.id) from Facility f
			where f.active = true
			and not exists (
				select 1 from PortalUserFacility puf
				where puf.facility.id = f.id and puf.active = true)
			""")
	long countActiveFacilitiesWithNoActivePortalMapping();

	@Query("select count(puf) from PortalUserFacility puf where puf.active = true")
	long countActivePortalUserFacilityRows();

	@Query("""
			select puf from PortalUserFacility puf
			join fetch puf.facility f
			join fetch f.block b
			join fetch b.district d
			where puf.portalUser.id = :portalUserId and puf.active = true
			""")
	List<PortalUserFacility> findActiveByPortalUserIdWithGeo(@Param("portalUserId") Long portalUserId);
}
