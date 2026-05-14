package com.sukrtya.siwan.web;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.sukrtya.siwan.master.UserMasterContextService;
import com.sukrtya.siwan.portal.PortalUser;
import com.sukrtya.siwan.portal.PortalUserRepository;
import com.sukrtya.siwan.web.dto.MasterContextResponse;

@RestController
@RequestMapping("/api/me")
public class UserMasterContextController {

	private final PortalUserRepository portalUserRepository;
	private final UserMasterContextService userMasterContextService;

	public UserMasterContextController(
			PortalUserRepository portalUserRepository,
			UserMasterContextService userMasterContextService) {
		this.portalUserRepository = portalUserRepository;
		this.userMasterContextService = userMasterContextService;
	}

	/**
	 * Geography (district, block, facility) plus active facility staff (CHO, ANM, ASHA facilitator, ASHA)
	 * for this user. {@code ADMIN}: all facilities. {@code DATA_COLLECTOR}: facilities linked in
	 * {@code portal_user_facility} only.
	 */
	@GetMapping("/master-context")
	public MasterContextResponse masterContext(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
		}
		PortalUser user = portalUserRepository.findByUsernameIgnoreCaseWithRole(authentication.getName())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unknown user"));
		return userMasterContextService.buildFor(user);
	}
}
