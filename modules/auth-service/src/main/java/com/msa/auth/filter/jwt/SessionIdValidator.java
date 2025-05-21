package com.msa.auth.filter.jwt;

import com.msa.auth.store.RefreshTokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SessionIdValidator {
    private final RefreshTokenStore refreshTokenStore;

    public Mono<Boolean> isValid(String userId, String sessionId) {
        return refreshTokenStore.getSessionId(userId)
                .map(sessionId::equals)
                .defaultIfEmpty(false);
    }
}