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
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoOAuth2ProviderService implements OAuth2ProviderService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    @Override
    public String providerName() {
        return "kakao";
    }

    @Override
    public OAuthUser exchange(String code) {
        String accessToken = getAccessToken(code);
        return getProfile(accessToken);
    }

    private String getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String url = "https://kauth.kakao.com/oauth/token";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        String formData = UriComponentsBuilder.newInstance()
                .queryParams(CollectionUtils.toMultiValueMap(body))
                .build()
                .getQuery();

        HttpEntity<String> request = new HttpEntity<>(formData, headers);

        ResponseEntity<JsonNode> response = restTemplate.postForEntity(url, request, JsonNode.class);
        JsonNode json = response.getBody();
        if (json == null || json.get("access_token") == null) {
            throw new IllegalStateException("카카오 토큰 요청 실패");
        }

        return json.get("access_token").asText();
    }

    private OAuthUser getProfile(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                request,
                JsonNode.class
        );

        JsonNode profile = response.getBody();
        if (profile == null || profile.get("id") == null) {
            throw new IllegalStateException("카카오 프로필 조회 실패");
        }

        return new OAuthUser(
                null,
                "kakao",
                profile.get("id").asText(),
                profile.path("kakao_account").path("email").asText(""),
                profile.path("properties").path("nickname").asText("")
        );
    }
}
