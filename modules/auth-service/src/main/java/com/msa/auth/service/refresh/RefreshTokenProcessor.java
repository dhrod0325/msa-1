package com.msa.auth.service.refresh;

import com.msa.auth.exception.UnauthorizedException;
import com.msa.auth.store.RefreshTokenStore;
import com.msa.common.jwt.AuthTokenResponse;
import com.msa.common.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RefreshTokenProcessor {
    private final RefreshTokenStore refreshTokenStore;
    private final JwtProvider jwtProvider;

    public Mono<AuthTokenResponse> refresh(String userId, String refreshToken) {
        return refreshTokenStore.validate(userId, refreshToken)
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new UnauthorizedException()))
                .flatMap(valid -> {
                    String access = jwtProvider.generateAccessToken(userId, "ROLE_USER");
                    return Mono.just(new AuthTokenResponse(access, refreshToken));
                });
    }
}
