package com.msa.auth.filter.jwt;

import com.msa.common.jwt.JwtProvider;
import com.msa.common.jwt.JwtResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtValidator {
    private final JwtProvider jwtProvider;

    public JwtResult validate(String token) {
        JwtResult result = jwtProvider.validate(token);

        if (!result.isValid()) {
            return null;
        }

        return result;
    }
}
