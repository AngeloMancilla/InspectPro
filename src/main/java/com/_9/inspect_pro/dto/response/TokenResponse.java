package com._9.inspect_pro.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        Long expiresIn,
        String tokenType
) {
    public static TokenResponse of(String accessToken, String refreshToken) {
        return new TokenResponse(
                accessToken,
                refreshToken,
                900L, // 15 minutes in seconds
                "Bearer"
        );
    }
}
