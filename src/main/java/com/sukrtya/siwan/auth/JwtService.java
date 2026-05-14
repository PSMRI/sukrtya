package com.sukrtya.siwan.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private final SecretKey signingKey;
	private final int validitySeconds;

	public JwtService(
			@Value("${app.jwt.secret}") String rawSecret,
			@Value("${app.jwt.access-token-validity-seconds:28800}") int validitySeconds) {
		this.signingKey = deriveKey(rawSecret);
		this.validitySeconds = validitySeconds;
	}

	private static SecretKey deriveKey(String rawSecret) {
		byte[] bytes = rawSecret.getBytes(StandardCharsets.UTF_8);
		if (bytes.length < 32) {
			try {
				bytes = MessageDigest.getInstance("SHA-256").digest(bytes);
			}
			catch (NoSuchAlgorithmException e) {
				throw new IllegalStateException(e);
			}
		}
		else {
			bytes = Arrays.copyOf(bytes, 32);
		}
		return Keys.hmacShaKeyFor(bytes);
	}

	public String generateAccessToken(String username, long userId, String portalRoleName) {
		long now = System.currentTimeMillis();
		Date issued = new Date(now);
		Date exp = new Date(now + validitySeconds * 1000L);
		return Jwts.builder()
				.subject(username)
				.claim("uid", userId)
				.claim("role", portalRoleName)
				.issuedAt(issued)
				.expiration(exp)
				.signWith(signingKey)
				.compact();
	}

	public Optional<Claims> parseValidClaims(String token) {
		try {
			return Optional.of(
					Jwts.parser()
							.verifyWith(signingKey)
							.build()
							.parseSignedClaims(token)
							.getPayload());
		}
		catch (JwtException | IllegalArgumentException e) {
			return Optional.empty();
		}
	}

	public int getValiditySeconds() {
		return validitySeconds;
	}
}
