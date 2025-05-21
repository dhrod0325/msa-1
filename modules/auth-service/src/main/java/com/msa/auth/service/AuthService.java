package com.msa.auth.service;

import com.msa.auth.dto.LoginRequest;
import com.msa.auth.exception.UnauthorizedException;
import com.msa.auth.repository.UserRepository;
import com.msa.auth.store.RefreshTokenStore;
import com.msa.common.jwt.AuthTokenResponse;
import com.msa.common.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtProvider jwtProvider;
    private final RefreshTokenStore refreshTokenStore;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<AuthTokenResponse> login(LoginRequest request) {
        return userRepository.findByUsername(request.getUsername())
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .switchIfEmpty(Mono.error(new UnauthorizedException()))
                .flatMap(user -> {
                    String access = jwtProvider.generateAccessToken(user.getId() + "", user.getRole());
                    String refresh = jwtProvider.generateRefreshToken(user.getId() + "", user.getRole());

                    return refreshTokenStore.save(user.getId() + "", refresh).thenReturn(new AuthTokenResponse(access, refresh));
                });
    }

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
