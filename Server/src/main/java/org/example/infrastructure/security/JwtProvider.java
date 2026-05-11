package org.example.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.example.util.PropertiesLoader;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Properties;

public class JwtProvider implements TokenProvider {
    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;
    private final int accessValidityInSeconds;
    private final int refreshValidityInSeconds;

    public JwtProvider() {
        PropertiesLoader propertiesLoader = new PropertiesLoader();
        Properties secretProperties = propertiesLoader.load("application-secret.properties");
        jwtAccessSecret = new SecretKeySpec(secretProperties.getProperty("jwt.secret.access").getBytes(), "HmacSHA256");
        jwtRefreshSecret = new SecretKeySpec(secretProperties.getProperty("jwt.secret.refresh").getBytes(), "HmacSHA256");

        Properties properties = propertiesLoader.load("application.properties");
        accessValidityInSeconds = Integer.parseInt(properties.getProperty("jwt.access.validityInSeconds"));
        refreshValidityInSeconds = Integer.parseInt(properties.getProperty("jwt.refresh.validityInSeconds"));
    }

    @Override
    public String generateAccessToken(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        Instant accessExpirationInstant = now.plusSeconds(accessValidityInSeconds)
                .atZone(ZoneId.systemDefault()).toInstant();
        Date accessExpiration = Date.from(accessExpirationInstant);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .expiration(accessExpiration)
                .signWith(jwtAccessSecret)
                .compact();
    }

    @Override
    public String generateRefreshToken(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        Instant refreshExpirationInstant = now.plusSeconds(refreshValidityInSeconds)
                .atZone(ZoneId.systemDefault()).toInstant();
        Date refreshExpiration = Date.from(refreshExpirationInstant);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .expiration(refreshExpiration)
                .signWith(jwtRefreshSecret)
                .compact();
    }

    @Override
    public boolean isValidateAccessToken(String accessToken) {
        return isValidateToken(accessToken, jwtAccessSecret);
    }

    @Override
    public boolean isValidateRefreshToken(String refreshToken) {
        return isValidateToken(refreshToken, jwtRefreshSecret);
    }

    private boolean isValidateToken(String token, SecretKey secret) {
        try {
            Jwts.parser()
                    .verifyWith(secret)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getClaims(String token, SecretKey secret) {
        return Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public Long getUserIdFromAccessToken(String token) {
        return Long.parseLong(getAccessClaims(token).getSubject());
    }

    @Override
    public Long getUserIdFromRefreshToken(String token) {
        return Long.parseLong(getRefreshClaims(token).getSubject());
    }

    @Override
    public void invalidateRefreshToken(String token) {
        // TODO add method realization
    }

    @Override
    public void invalidateAccessToken(String token) {
        // TODO add method realization
    }

    private Claims getAccessClaims(String token) {
        return getClaims(token, jwtAccessSecret);
    }

    private Claims getRefreshClaims(String token) {
        return getClaims(token, jwtRefreshSecret);
    }
}
