package org.example.infrastructure.security;

public interface TokenProvider {
    String generateAccessToken(Long userId);

    String generateRefreshToken(Long userId);

    boolean isValidateAccessToken(String token);

    boolean isValidateRefreshToken(String token);

    Long getUserIdFromAccessToken(String token);

    Long getUserIdFromRefreshToken(String token);
}
