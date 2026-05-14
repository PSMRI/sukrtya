package com.sukrtya.siwan.portal;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PortalUserRepository extends JpaRepository<PortalUser, Long> {

	Optional<PortalUser> findByUsernameIgnoreCase(String username);

	Optional<PortalUser> findByMobilePhone(String mobilePhone);

	@Query("select u from PortalUser u join fetch u.portalRole where lower(u.username) = lower(:username)")
	Optional<PortalUser> findByUsernameIgnoreCaseWithRole(@Param("username") String username);

	long count();

	long countByAccountEnabledTrue();
}
