package com.msa.auth.service.logout;

import com.msa.auth.store.RefreshTokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LogoutProcessor {
    private final RefreshTokenStore refreshTokenStore;

    public Mono<Void> logout(String userId, String accessToken) {
        return refreshTokenStore.delete(userId)
                .then(refreshTokenStore.blacklistAccessToken(accessToken))
                .then(refreshTokenStore.deleteSession(userId));
    }
}