package com.msa.auth.controller;

import com.msa.auth.dto.AuthTokenResponse;
import com.msa.auth.oauth2.OAuth2Service;
import com.msa.auth.store.RefreshTokenStore;
import com.msa.common.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RestController
@RequestMapping("/auth/oauth2")
@RequiredArgsConstructor
public class OAuth2AuthController {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenStore refreshTokenStore;
    private final OAuth2Service oAuth2Service;

    @GetMapping("/authorization/{provider}")
    public void redirectToProvider(@PathVariable String provider, HttpServletResponse response) throws IOException {
        ClientRegistration registration = clientRegistrationRepository.findByRegistrationId(provider);
        if (registration == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown provider: " + provider);
            return;
        }

        String authorizationUri = getAuthorizationUri(registration, provider);
        response.sendRedirect(authorizationUri);
    }

    @GetMapping("/callback/{provider}")
    public ResponseEntity<?> callback(@PathVariable String provider, @RequestParam String code) {
        log.info("call callback {}", provider);

        var oAuthUser = oAuth2Service.exchangeCodeForUserBlocking(provider, code);
        if (oAuthUser == null) {
            return ResponseEntity.status(400).body("OAuth 사용자 정보를 가져올 수 없습니다.");
        }

        var user = oAuth2Service.findUserByOAuthUserIdBlocking(oAuthUser.getProviderUserId());

        if (user == null) {
            oAuth2Service.publishSignupEventBlocking(oAuthUser);
            return ResponseEntity.status(404).body("사용자가 존재하지 않아 가입 이벤트를 발행했습니다.");
        }

        String accessToken = jwtProvider.generateAccessToken(user.getId() + "", user.getRole());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId() + "", user.getRole());

        log.info("callback : {}, {}", accessToken, refreshToken);

        refreshTokenStore.save(user.getId() + "", refreshToken); // 동기 저장

        return ResponseEntity.ok(new AuthTokenResponse(accessToken, refreshToken));
    }

    private String getAuthorizationUri(ClientRegistration registration, String provider) {
        String baseAuthUri = switch (provider) {
            case "kakao" -> "https://kauth.kakao.com/oauth/authorize";
            case "naver" -> "https://nid.naver.com/oauth2.0/authorize";
            case "google" -> "https://accounts.google.com/o/oauth2/v2/auth";
            default -> registration.getProviderDetails().getAuthorizationUri();
        };

        return UriComponentsBuilder.fromUriString(baseAuthUri)
                .queryParam("response_type", "code")
                .queryParam("client_id", registration.getClientId())
                .queryParam("redirect_uri", registration.getRedirectUri())
                .queryParam("scope", String.join(",", registration.getScopes()))
                .build(true)
                .toUriString();
    }
}
