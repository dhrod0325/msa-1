package com.msa.auth.controller;

import com.msa.auth.dto.AuthTokenResponse;
import com.msa.auth.dto.LoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@Slf4j
@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void 로그인실패_아이디공백() {
        LoginRequest request = new LoginRequest("", "password");

        webTestClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void 로그인실패_패스워드공백() {
        LoginRequest request = new LoginRequest("admin123", "");

        webTestClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void 로그인성공() {
        LoginRequest request = new LoginRequest("admin", "12345");

        AuthTokenResponse response = webTestClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(AuthTokenResponse.class)
                .returnResult()
                .getResponseBody();

        log.info("로그인 응답: {}", response);
    }
}
