package com.msa.auth.oauth2.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.msa.auth.entity.OAuthUser;
import com.msa.auth.oauth2.OAuth2ProviderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class NaverOAuth2ProviderService implements OAuth2ProviderService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String redirectUri;

    @Override
    public String providerName() {
        return "naver";
    }

    @Override
    public OAuthUser exchange(String code) {
        String accessToken = getAccessToken(code);
        return getProfile(accessToken);
    }

    private String getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                "https://nid.naver.com/oauth2.0/token", request, JsonNode.class);

        JsonNode json = response.getBody();
        if (json == null || json.get("access_token") == null) {
            throw new IllegalStateException("네이버 토큰 요청 실패");
        }

        return json.get("access_token").asText();
    }

    private OAuthUser getProfile(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                request,
                JsonNode.class
        );

        JsonNode root = response.getBody();
        if (root == null || root.get("response") == null) {
            throw new IllegalStateException("네이버 프로필 조회 실패");
        }

        JsonNode profile = root.get("response");

        return new OAuthUser(
                null,
                "naver",
                profile.get("id").asText(),
                profile.path("email").asText(""),
                profile.path("name").asText("")
        );
    }
}
