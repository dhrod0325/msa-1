package com.msa.auth.service;

import com.msa.auth.dto.LoginRequest;
import com.msa.auth.dto.SignupRequest;
import com.msa.auth.repository.ReactiveUserRepository;
import com.msa.common.jwt.AuthTokenResponse;
import com.msa.common.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final ReactiveUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public Mono<AuthTokenResponse> login(LoginRequest req) {
        return userRepository.findByUsername(req.getUsername())
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                .flatMap(user -> {
                    if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
                        return Mono.error(new RuntimeException("Invalid password"));
                    }

                    String accessToken = jwtProvider.generateToken(
                            user.getUsername(),
                            Map.of("roles", user.getRole()),
                            jwtProvider.getJwtProperties().getAccessTokenValiditySeconds()
                    );

                    String refreshToken = jwtProvider.generateToken(
                            user.getUsername(),
                            Map.of(),
                            jwtProvider.getJwtProperties().getRefreshTokenValiditySeconds()
                    );

                    return Mono.just(new AuthTokenResponse(accessToken, refreshToken));
                });
    }

    public Mono<String> signup(SignupRequest req) {
        return userRepository.findByUsername(req.getUsername())
                .flatMap(existing -> Mono.error(new RuntimeException("User already exists")));
    }
}
