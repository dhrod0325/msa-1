package com.msa.auth.controller;

import com.msa.auth.dto.LoginRequest;
import com.msa.auth.service.AuthService;
import com.msa.common.jwt.AuthTokenResponse;
import com.msa.common.jwt.JwtProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    private final JwtProvider jwtProvider;

    @PostMapping("/login")
    public Mono<AuthTokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public Mono<AuthTokenResponse> refresh(@RequestHeader("user-id") String userId,
                                           @RequestHeader("refresh-token") String refreshToken) {
        return authService.refresh(userId, refreshToken);
    }

    @PostMapping("/logout")
    public Mono<Void> logout(@RequestHeader("user-id") String userId) {
        return authService.logout(userId);
    }

    @GetMapping("/check")
    public Mono<Boolean> check(@RequestHeader("Authorization") String bearerToken) {
        return Mono.justOrEmpty(bearerToken)
                .map(bToken -> bToken.replace("Bearer ", ""))
                .map(token -> jwtProvider.validate(token).isValid());
    }
}
