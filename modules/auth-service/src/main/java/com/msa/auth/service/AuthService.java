package com.msa.auth.service;

import com.msa.auth.dto.LoginRequest;
import com.msa.auth.service.login.LoginProcessor;
import com.msa.auth.service.logout.LogoutProcessor;
import com.msa.auth.service.refresh.RefreshTokenProcessor;
import com.msa.common.jwt.AuthTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final LoginProcessor loginProcessor;
    private final RefreshTokenProcessor refreshTokenProcessor;
    private final LogoutProcessor logoutProcessor;

    public Mono<AuthTokenResponse> login(LoginRequest request) {
        return loginProcessor.login(request);
    }

    public Mono<AuthTokenResponse> refresh(String userId, String refreshToken) {
        return refreshTokenProcessor.refresh(userId, refreshToken);
    }

    public Mono<Void> logout(String userId, String accessToken) {
        return logoutProcessor.logout(userId, accessToken);
    }
}
