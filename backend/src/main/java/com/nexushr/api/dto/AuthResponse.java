package com.nexushr.api.dto;

public record AuthResponse(
        String accessToken,
        String role,
        String displayName
) {
}
