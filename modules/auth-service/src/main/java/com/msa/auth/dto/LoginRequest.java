package com.msa.auth.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class LoginRequest {
    @NotEmpty
    private String username;

    @NotEmpty
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}