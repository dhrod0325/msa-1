package com.msa.auth.store;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RefreshTokenStore {
    private final ReactiveStringRedisTemplate redisTemplate;

    public Mono<Void> save(String userId, String token) {
        return redisTemplate.opsForValue()
                .set("refresh:" + userId, token, Duration.ofDays(7)).then();
    }

    public Mono<Boolean> validate(String userId, String token) {
        return redisTemplate.opsForValue()
                .get("refresh:" + userId)
                .map(saved -> saved.equals(token));
    }

    public Mono<Void> delete(String userId) {
        return redisTemplate.delete("refresh:" + userId).then();
    }
}