package com.msa.auth.controller;

import com.msa.auth.dto.LoginRequest;
import com.msa.auth.exception.UnauthorizedException;
import com.msa.auth.service.AuthService;
import com.msa.common.jwt.AuthTokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "인증 API", description = "로그인 / 리프레시 / 로그아웃 처리")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그인", description = "아이디/비밀번호로 JWT 발급")
    @PostMapping("/login")
    public AuthTokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @Operation(summary = "토큰 재발급", description = "리프레시 토큰을 통한 액세스 토큰 재발급")
    @PostMapping("/refresh")
    public AuthTokenResponse refresh(@RequestHeader("user-id") String userId,
                                     @RequestHeader("refresh-token") String refreshToken) {
        return authService.refresh(userId, refreshToken);
    }

    @Operation(summary = "로그아웃", description = "로그아웃 시 리프레시 토큰 및 세션 삭제")
    @PostMapping("/logout")
    public void logout(@RequestHeader("user-id") String userId,
                       @RequestHeader("Authorization") String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new UnauthorizedException("Authorization 누락 또는 형식 오류");
        }

        String token = authorization.substring(7);
        authService.logout(userId, token);
    }
}
