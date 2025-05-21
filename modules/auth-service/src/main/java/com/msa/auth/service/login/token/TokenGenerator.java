package com.msa.auth.service.login.token;

import com.msa.auth.entity.User;
import com.msa.auth.store.RefreshTokenStore;
import com.msa.common.jwt.AuthTokenResponse;
import com.msa.common.jwt.JwtProvider;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TokenGenerator {
    private final JwtProvider jwtProvider;
    private final RefreshTokenStore refreshTokenStore;

    public Mono<AuthTokenResponse> generate(User user) {
        String sessionId = UUID.randomUUID().toString();
        String access = jwtProvider.generateAccessToken(user.getId().toString(), user.getRole(), sessionId);
        String refresh = jwtProvider.generateRefreshToken(user.getId().toString(), user.getRole());

        return refreshTokenStore.save(user.getId().toString(), refresh)
                .then(refreshTokenStore.saveSession(user.getId().toString(), sessionId))
                .thenReturn(new AuthTokenResponse(access, refresh));
    }
}