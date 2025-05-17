package com.msa.auth.controller;

import com.msa.auth.dto.LoginRequest;
import com.msa.auth.dto.SignupRequest;
import com.msa.auth.service.AuthService;
import com.msa.common.jwt.AuthTokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public Mono<ResponseEntity<String>> signup(@Valid @RequestBody Mono<SignupRequest> requestMono) {
        return requestMono
                .flatMap(request -> authService.signup(request).map(ResponseEntity::ok));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthTokenResponse>> login(@RequestBody Mono<LoginRequest> requestMono) {
        return requestMono
                .flatMap(authService::login)
                .map(ResponseEntity::ok);
    }
}
