package com.msa.auth.service.oauth2.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.msa.auth.entity.OAuthUser;
import com.msa.auth.service.oauth2.OAuth2ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class GoogleOAuth2ProviderService implements OAuth2ProviderService {
    private final WebClient webClient;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    @Override
    public String providerName() {
        return "google";
    }

    @Override
    public Mono<OAuthUser> exchange(String code) {
        return getAccessToken(code)
                .flatMap(this::getProfile);
    }

    private Mono<String> getAccessToken(String code) {
        return webClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                        .with("client_id", clientId)
                        .with("redirect_uri", redirectUri)
                        .with("code", code))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> json.get("access_token").asText());
    }

    private Mono<OAuthUser> getProfile(String token) {
        return webClient.get()
                .uri("https://www.googleapis.com/oauth2/v2/userinfo")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(profile -> new OAuthUser(
                        null,
                        "google",
                        profile.get("id").asText(),
                        profile.path("email").asText(""),
                        profile.path("name").asText("")
                ));
    }
}
