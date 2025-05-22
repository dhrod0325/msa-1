package com.msa.auth.oauth2.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.msa.auth.entity.OAuthUser;
import com.msa.auth.oauth2.OAuth2ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class GoogleOAuth2ProviderService implements OAuth2ProviderService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Override
    public String providerName() {
        return "google";
    }

    // 동기 방식으로 OAuthUser 반환
    @Override
    public OAuthUser exchange(String code) {
        String accessToken = getAccessToken(code);
        return getProfile(accessToken);
    }

    private String getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String tokenUrl = "https://oauth2.googleapis.com/token";

        HttpEntity<String> request = new HttpEntity<>(
                UriComponentsBuilder.newInstance()
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", clientId)
                        .queryParam("client_secret", clientSecret)
                        .queryParam("redirect_uri", redirectUri)
                        .queryParam("code", code)
                        .build()
                        .getQuery(),
                headers
        );

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                request,
                JsonNode.class
        );

        JsonNode json = response.getBody();
        if (json == null || json.get("access_token") == null) {
            throw new IllegalStateException("Access token 응답 오류");
        }

        return json.get("access_token").asText();
    }

    private OAuthUser getProfile(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v2/userinfo",
                HttpMethod.GET,
                request,
                JsonNode.class
        );

        JsonNode profile = response.getBody();
        if (profile == null || profile.get("id") == null) {
            throw new IllegalStateException("프로필 정보 응답 오류");
        }

        return new OAuthUser(
                null,
                "google",
                profile.get("id").asText(),
                profile.path("email").asText(""),
                profile.path("name").asText("")
        );
    }
}
