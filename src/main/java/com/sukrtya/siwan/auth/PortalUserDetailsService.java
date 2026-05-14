package com.sukrtya.siwan.auth;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sukrtya.siwan.portal.PortalUser;
import com.sukrtya.siwan.portal.PortalUserRepository;

@Service
public class PortalUserDetailsService implements UserDetailsService {

	private final PortalUserRepository portalUserRepository;

	public PortalUserDetailsService(PortalUserRepository portalUserRepository) {
		this.portalUserRepository = portalUserRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		PortalUser user = portalUserRepository.findByUsernameIgnoreCaseWithRole(username.trim())
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
		String role = user.getPortalRole().getRoleName();
		String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
		return User.builder()
				.username(user.getUsername())
				.password(user.getPasswordHash())
				.disabled(!user.isAccountEnabled())
				.authorities(authority)
				.build();
	}
}
