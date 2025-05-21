package com.msa.auth.service;

import com.msa.auth.dto.LoginRequest;
import com.msa.auth.exception.UnauthorizedException;
import com.msa.auth.service.login.processor.LoginProcessor;
import com.msa.auth.store.RefreshTokenStore;
import com.msa.common.jwt.AuthTokenResponse;
import com.msa.common.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtProvider jwtProvider;
    private final RefreshTokenStore refreshTokenStore;

    private final LoginProcessor loginProcessor;

    public Mono<AuthTokenResponse> login(LoginRequest request) {
        return loginProcessor.login(request);
    }

    //TODO 추후 확장 해야 하는 경우가 생긴다면 리팩토링 고려할것
    public Mono<AuthTokenResponse> refresh(String userId, String refreshToken) {
        return refreshTokenStore.validate(userId, refreshToken)
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new UnauthorizedException()))
                .flatMap(valid -> {
                    String access = jwtProvider.generateAccessToken(userId, "ROLE_USER");
                    return Mono.just(new AuthTokenResponse(access, refreshToken));
                });
    }

    public Mono<Void> logout(String userId) {
        return refreshTokenStore.delete(userId);
    }
}
