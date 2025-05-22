package com.msa.auth.store;

import com.msa.common.jwt.JwtProvider;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RefreshTokenStore {
    private static final String REFRESH_PREFIX = "refresh:";
    private static final String SESSION_PREFIX = "session:";
    private static final String BLACKLIST_PREFIX = "blacklist:access:";

    private final StringRedisTemplate redisTemplate;
    private final JwtProvider jwtProvider;

    public void save(String userId, String token) {
        redisTemplate.opsForValue().set(REFRESH_PREFIX + userId, token, Duration.ofDays(7));
    }

    public boolean validate(String userId, String token) {
        String saved = redisTemplate.opsForValue().get(REFRESH_PREFIX + userId);
        return token.equals(saved);
    }

    public void delete(String userId) {
        redisTemplate.delete(REFRESH_PREFIX + userId);
    }

    public void saveSession(String userId, String sessionId) {
        redisTemplate.opsForValue().set(SESSION_PREFIX + userId, sessionId, Duration.ofDays(1));
    }

    public void deleteSession(String userId) {
        redisTemplate.delete(SESSION_PREFIX + userId);
    }

    public String getSessionId(String userId) {
        return redisTemplate.opsForValue().get(SESSION_PREFIX + userId);
    }

    public void blacklistAccessToken(String accessToken) {
        long expirySeconds = jwtProvider.getRemainingSeconds(accessToken);
        if (expirySeconds > 0) {
            redisTemplate.opsForValue().set(BLACKLIST_PREFIX + accessToken, "1", Duration.ofSeconds(expirySeconds));
        }
    }

    public boolean isBlacklisted(String accessToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + accessToken));
    }
}
