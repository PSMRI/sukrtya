package com.sukrtya.siwan.web;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.sukrtya.siwan.auth.AuthService;
import com.sukrtya.siwan.auth.dto.LoginRequest;
import com.sukrtya.siwan.auth.dto.LoginResponse;
import com.sukrtya.siwan.auth.dto.MeResponse;
import com.sukrtya.siwan.portal.PortalUser;
import com.sukrtya.siwan.portal.PortalUserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;
	private final PortalUserRepository portalUserRepository;

	public AuthController(AuthService authService, PortalUserRepository portalUserRepository) {
		this.authService = authService;
		this.portalUserRepository = portalUserRepository;
	}

	@PostMapping("/login")
	public LoginResponse login(@Valid @RequestBody LoginRequest body) {
		try {
			return authService.login(body.username(), body.password());
		}
		catch (DisabledException e) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is disabled");
		}
		catch (AuthenticationException e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
		}
	}

	@GetMapping("/me")
	public MeResponse me(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
		}
		PortalUser user = portalUserRepository.findByUsernameIgnoreCaseWithRole(authentication.getName())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated"));
		return new MeResponse(
				user.getId(),
				user.getUsername(),
				user.getDisplayName(),
				user.getPortalRole().getRoleName());
	}
}
