package com.msa.auth.store;

import com.msa.common.jwt.JwtProvider;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RefreshTokenStore {
    private static final String REFRESH_PREFIX = "refresh:";
    private static final String SESSION_PREFIX = "session:";
    private static final String BLACKLIST_PREFIX = "blacklist:access:";

    private final ReactiveStringRedisTemplate redisTemplate;
    private final JwtProvider jwtProvider;

    public Mono<Void> save(String userId, String token) {
        return redisTemplate.opsForValue()
                .set(REFRESH_PREFIX + userId, token, Duration.ofDays(7))
                .then();
    }

    public Mono<Boolean> validate(String userId, String token) {
        return redisTemplate.opsForValue()
                .get(REFRESH_PREFIX + userId)
                .map(saved -> saved.equals(token))
                .defaultIfEmpty(false);
    }

    public Mono<Void> delete(String userId) {
        return redisTemplate.delete(REFRESH_PREFIX + userId).then();
    }

    public Mono<Void> saveSession(String userId, String sessionId) {
        return redisTemplate.opsForValue()
                .set(SESSION_PREFIX + userId, sessionId, Duration.ofDays(1))
                .then();
    }

    public Mono<Void> deleteSession(String userId) {
        return redisTemplate.delete(SESSION_PREFIX + userId).then();
    }

    public Mono<String> getSessionId(String userId) {
        return redisTemplate.opsForValue().get(SESSION_PREFIX + userId);
    }

    public Mono<Void> blacklistAccessToken(String accessToken) {
        long expirySeconds = jwtProvider.getRemainingSeconds(accessToken);
        if (expirySeconds <= 0) {
            return Mono.empty();
        }
        return redisTemplate.opsForValue()
                .set(BLACKLIST_PREFIX + accessToken, "1", Duration.ofSeconds(expirySeconds))
                .then();
    }

    public Mono<Boolean> isBlacklisted(String accessToken) {
        return redisTemplate.hasKey(BLACKLIST_PREFIX + accessToken);
    }
}