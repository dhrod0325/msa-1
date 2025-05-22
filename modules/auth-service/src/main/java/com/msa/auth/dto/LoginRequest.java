package com.msa.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "로그인 요청 DTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @Schema(description = "사용자 아이디", example = "admin")
    @NotEmpty
    private String username;

    @Schema(description = "비밀번호", example = "1234")
    @NotEmpty
    private String password;
}
