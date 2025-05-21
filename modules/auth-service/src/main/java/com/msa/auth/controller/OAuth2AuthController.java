package com.msa.auth.controller;

import com.msa.auth.dto.AuthTokenResponse;
import com.msa.auth.oauth2.OAuth2Service;
import com.msa.auth.store.RefreshTokenStore;
import com.msa.common.jwt.JwtProvider;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth/oauth2")
@RequiredArgsConstructor
public class OAuth2AuthController {
    private final ReactiveClientRegistrationRepository clientRegistrationRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenStore refreshTokenStore;
    private final OAuth2Service OAuth2Service;

    @GetMapping("/authorization/{provider}")
    public Mono<Void> redirectToProvider(@PathVariable String provider, ServerHttpResponse response) {
        return Mono.from(clientRegistrationRepository.findByRegistrationId(provider))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Unknown provider: " + provider)))
                .flatMap(registration -> {
                    String authorizationUri = getAuthorizationUri(registration, provider);

                    response.setStatusCode(HttpStatus.FOUND);
                    response.getHeaders().setLocation(URI.create(authorizationUri));
                    return response.setComplete();
                });
    }

    @GetMapping("/callback/{provider}")
    public Mono<ResponseEntity<AuthTokenResponse>> callback(@PathVariable String provider, @RequestParam String code) {
        return OAuth2Service
                .exchangeCodeForUser(provider, code)
                .flatMap(oAuthUser -> OAuth2Service.findUserByOAuthUserId(oAuthUser.getProviderUserId()))
                .flatMap(user -> {
                    String accessToken = jwtProvider.generateAccessToken(user.getId() + "", user.getRole());
                    String refreshToken = jwtProvider.generateRefreshToken(user.getId() + "", user.getRole());

                    return refreshTokenStore
                            .save(user.getId() + "", refreshToken)
                            .thenReturn(ResponseEntity.ok(new AuthTokenResponse(accessToken, refreshToken)));
                });
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
