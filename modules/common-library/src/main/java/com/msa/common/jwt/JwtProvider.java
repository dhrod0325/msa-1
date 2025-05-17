package com.msa.common.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    @Getter
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

    // access 토큰
    public String generateAccessToken(String subject, Map<String, Object> claims) {
        return generateToken(subject, claims, jwtProperties.getAccessTokenValiditySeconds());
    }

    // refresh 토큰
    public String generateRefreshToken(String subject) {
        return generateToken(subject, Map.of(), jwtProperties.getRefreshTokenValiditySeconds());
    }

    // 토큰 생성
    public String generateToken(String subject, Map<String, Object> claims, long expirySeconds) {
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirySeconds * 1000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 검증 후 claims 추출
    public Claims validateAndGetClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 사용자 정보 추출
    public String extractUsername(String token) {
        return validateAndGetClaims(token).getSubject();
    }

    // 검증 + 결과 객체 반환
    public JwtResult validate(String token) {
        try {
            Claims claims = validateAndGetClaims(token);
            String userId = claims.getSubject();
            String roles = claims.get("roles", String.class);
            return new JwtResult(userId, roles);
        } catch (JwtException e) {
            throw new JwtException();
        }
    }
}
