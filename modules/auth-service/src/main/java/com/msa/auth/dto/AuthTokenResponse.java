package com.msa.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
@Schema(description = "JWT 토큰 응답 DTO")
public class AuthTokenResponse {

    @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6...")
    private String accessToken;

    @Schema(description = "리프레시 토큰", example = "dGhpc2lzYXJlZnJlc2h0b2tlbg==")
    private String refreshToken;
}