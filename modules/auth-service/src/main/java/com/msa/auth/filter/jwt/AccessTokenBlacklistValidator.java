package com.msa.auth.filter.jwt;

import com.msa.auth.store.RefreshTokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AccessTokenBlacklistValidator {
    private final RefreshTokenStore refreshTokenStore;

    public Mono<Boolean> isBlacklisted(String token) {
        return refreshTokenStore.isBlacklisted(token);
    }
}