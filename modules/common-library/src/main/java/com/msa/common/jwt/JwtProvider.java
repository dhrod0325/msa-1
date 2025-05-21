package com.msa.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final JwtProperties jwtProperties;
    private Key key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 256 bits (32 bytes)");
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String subject, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", role);

        return generateAccessToken(subject, claims);
    }

    public String generateAccessToken(String subject, Map<String, Object> claims) {
        return generateToken(subject, claims, jwtProperties.getAccessTokenValiditySeconds());
    }

    public String generateAccessToken(String subject, String role, String sessionId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", role);
        claims.put("sessionId", sessionId);

        return generateAccessToken(subject, claims);
    }

    public String generateRefreshToken(String subject, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", role);

        return generateRefreshToken(subject, claims);
    }

    public String generateRefreshToken(String subject, Map<String, Object> claims) {
        return generateToken(subject, claims, jwtProperties.getRefreshTokenValiditySeconds());
    }

    public String generateToken(String subject, Map<String, Object> claims, long expirySeconds) {
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirySeconds * 1000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims validateAndGetClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public JwtResult validate(String token) {
        try {
            Claims claims = validateAndGetClaims(token);
            String userId = claims.getSubject();
            String roles = claims.get("roles", String.class);

            return new JwtResult(userId, roles, true);
        } catch (JwtException e) {
            log.error(e.getMessage(), e);

            return new JwtResult(null, null, false);
        }
    }
}
