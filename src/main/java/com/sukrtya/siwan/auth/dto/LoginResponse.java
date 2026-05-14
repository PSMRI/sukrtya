package com.sukrtya.siwan.auth.dto;

public record LoginResponse(
		String accessToken,
		String tokenType,
		long expiresInSeconds,
		String role,
		String username,
		String displayName) {
}
