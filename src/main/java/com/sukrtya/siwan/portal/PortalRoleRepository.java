package com.sukrtya.siwan.portal;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PortalRoleRepository extends JpaRepository<PortalRole, Long> {

	Optional<PortalRole> findByRoleNameIgnoreCase(String roleName);
}
