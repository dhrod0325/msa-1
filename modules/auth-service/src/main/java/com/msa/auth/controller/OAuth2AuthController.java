package com.msa.auth.controller;

import com.msa.auth.dto.AuthTokenResponse;
import com.msa.auth.oauth2.OAuth2LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth/oauth2")
@RequiredArgsConstructor
public class OAuth2AuthController {
    private final OAuth2LoginService auth2LoginService;

    @Operation(
            summary = "OAuth2 인증 요청",
            description = "provider(kakao, naver, google)에 따라 인증 페이지로 리다이렉트합니다.",
            parameters = {
                    @Parameter(name = "provider", description = "OAuth2 Provider", example = "kakao")
            },
            responses = {
                    @ApiResponse(responseCode = "302", description = "해당 Provider 인증 페이지로 리다이렉트됨"),
                    @ApiResponse(responseCode = "400", description = "지원되지 않는 Provider")
            }
    )
    @GetMapping("/authorization/{provider}")
    public void redirectToProvider(@PathVariable String provider, HttpServletResponse response) throws IOException {
        String authorizationUri = auth2LoginService.getAuthorizationUri(provider);
        response.sendRedirect(authorizationUri);
    }

    @Operation(
            summary = "OAuth2 콜백",
            description = "OAuth2 provider에서 인증 후 전달된 code로 사용자 정보를 가져와 로그인 처리합니다.",
            parameters = {
                    @Parameter(name = "provider", description = "OAuth2 Provider", example = "kakao"),
                    @Parameter(name = "code", description = "Authorization Code", example = "abc123")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "JWT 토큰 반환"),
                    @ApiResponse(responseCode = "400", description = "OAuth 사용자 정보 없음"),
                    @ApiResponse(responseCode = "404", description = "가입되지 않은 사용자 → 가입 이벤트 발행")
            }
    )
    @GetMapping("/callback/{provider}")
    public ResponseEntity<?> callback(@PathVariable String provider, @RequestParam String code) {
        AuthTokenResponse response = auth2LoginService.handleCallback(provider, code);
        return ResponseEntity.ok(response);
    }
}
