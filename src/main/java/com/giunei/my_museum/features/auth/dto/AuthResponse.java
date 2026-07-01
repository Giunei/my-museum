package com.giunei.my_museum.features.auth.dto;

public record AuthResponse(
		String token,
		String accessToken,
		String refreshToken,
		String tokenType,
		long expiresInSeconds
) {

	public static AuthResponse from(String accessToken, String refreshToken, long expiresInSeconds) {
		return new AuthResponse(accessToken, accessToken, refreshToken, "Bearer", expiresInSeconds);
	}
}
