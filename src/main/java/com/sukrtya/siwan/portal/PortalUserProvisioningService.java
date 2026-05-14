package com.sukrtya.siwan.portal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sukrtya.siwan.master.Facility;

@Service
public class PortalUserProvisioningService {

	private final PortalRoleRepository portalRoleRepository;
	private final PortalUserRepository portalUserRepository;
	private final PortalUserFacilityRepository portalUserFacilityRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${app.seed.default-admin-username:admin}")
	private String defaultAdminUsername;

	@Value("${app.seed.default-admin-password:Admin@123}")
	private String defaultAdminPassword;

	@Value("${app.seed.cho-collector-default-password:User@123}")
	private String choCollectorDefaultPassword;

	public PortalUserProvisioningService(
			PortalRoleRepository portalRoleRepository,
			PortalUserRepository portalUserRepository,
			PortalUserFacilityRepository portalUserFacilityRepository,
			PasswordEncoder passwordEncoder) {
		this.portalRoleRepository = portalRoleRepository;
		this.portalUserRepository = portalUserRepository;
		this.portalUserFacilityRepository = portalUserFacilityRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public void ensureDefaultAdmin() {
		if (portalUserRepository.findByUsernameIgnoreCase(defaultAdminUsername.trim()).isPresent()) {
			return;
		}
		PortalRole adminRole = portalRoleRepository.findByRoleNameIgnoreCase(PortalRole.ADMIN)
				.orElseThrow(() -> new IllegalStateException("Portal role ADMIN is missing; apply database migrations."));
		PortalUser user = new PortalUser();
		user.setUsername(defaultAdminUsername.trim());
		user.setPasswordHash(passwordEncoder.encode(defaultAdminPassword));
		user.setPortalRole(adminRole);
		user.setDisplayName("Administrator");
		user.setAccountEnabled(true);
		portalUserRepository.save(user);
	}

	/**
	 * Creates or updates a DATA_COLLECTOR login for CHO mobile collection: {@code username} and
	 * {@code mobile_phone} are the normalized 10-digit mobile; default password on first creation
	 * is {@link #choCollectorDefaultPassword}. Ensures an active {@code portal_user_facility} row
	 * for the CHO's facility.
	 */
	@Transactional
	public void ensureChoDataCollectorForFacility(String normalizedMobileTenDigits, String displayName, Facility facility) {
		if (normalizedMobileTenDigits == null || normalizedMobileTenDigits.isBlank()) {
			return;
		}
		String username = normalizedMobileTenDigits.trim();
		PortalUser user = portalUserRepository.findByMobilePhone(username)
				.or(() -> portalUserRepository.findByUsernameIgnoreCase(username))
				.orElse(null);

		if (user != null && !PortalRole.DATA_COLLECTOR.equalsIgnoreCase(user.getPortalRole().getRoleName())) {
			return;
		}

		if (user == null) {
			PortalRole collectorRole = portalRoleRepository.findByRoleNameIgnoreCase(PortalRole.DATA_COLLECTOR)
					.orElseThrow(() -> new IllegalStateException("Portal role DATA_COLLECTOR is missing; apply database migrations."));
			user = new PortalUser();
			user.setUsername(username);
			user.setMobilePhone(username);
			user.setPasswordHash(passwordEncoder.encode(choCollectorDefaultPassword));
			user.setPortalRole(collectorRole);
			user.setDisplayName(displayName != null && !displayName.isBlank() ? displayName.trim() : "CHO");
			user.setAccountEnabled(true);
			user = portalUserRepository.save(user);
		}
		else {
			if (user.getDisplayName() == null || user.getDisplayName().isBlank()) {
				user.setDisplayName(displayName != null && !displayName.isBlank() ? displayName.trim() : "CHO");
				portalUserRepository.save(user);
			}
		}

		final PortalUser portalUser = user;
		portalUserFacilityRepository
				.findByPortalUser_IdAndFacility_Id(portalUser.getId(), facility.getId())
				.ifPresentOrElse(mapping -> {
					if (!mapping.isActive()) {
						mapping.setActive(true);
						portalUserFacilityRepository.save(mapping);
					}
				}, () -> {
					PortalUserFacility mapping = new PortalUserFacility();
					mapping.setPortalUser(portalUser);
					mapping.setFacility(facility);
					mapping.setActive(true);
					long existingPrimary = portalUserFacilityRepository.countByPortalUser_IdAndActiveTrueAndPrimaryTrue(portalUser.getId());
					mapping.setPrimary(existingPrimary == 0);
					portalUserFacilityRepository.save(mapping);
				});
	}
}
