package com.sukrtya.siwan.auth;

import java.time.Instant;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sukrtya.siwan.auth.dto.LoginResponse;
import com.sukrtya.siwan.portal.PortalUser;
import com.sukrtya.siwan.portal.PortalUserRepository;

@Service
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final PortalUserRepository portalUserRepository;
	private final JwtService jwtService;

	public AuthService(
			AuthenticationManager authenticationManager,
			PortalUserRepository portalUserRepository,
			JwtService jwtService) {
		this.authenticationManager = authenticationManager;
		this.portalUserRepository = portalUserRepository;
		this.jwtService = jwtService;
	}

	@Transactional
	public LoginResponse login(String username, String password) {
		Authentication authRequest = new UsernamePasswordAuthenticationToken(username.trim(), password);
		Authentication authenticated = authenticationManager.authenticate(authRequest);
		PortalUser user = portalUserRepository.findByUsernameIgnoreCaseWithRole(authenticated.getName())
				.orElseThrow();
		user.setLastLoginAt(Instant.now());
		String roleName = user.getPortalRole().getRoleName();
		String accessToken = jwtService.generateAccessToken(user.getUsername(), user.getId(), roleName);
		return new LoginResponse(
				accessToken,
				"Bearer",
				jwtService.getValiditySeconds(),
				roleName,
				user.getUsername(),
				user.getDisplayName());
	}
}
