package com.sukrtya.siwan.auth.dto;

public record MeResponse(long userId, String username, String displayName, String role) {
}
