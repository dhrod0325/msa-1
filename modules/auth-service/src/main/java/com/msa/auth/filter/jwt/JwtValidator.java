package com.msa.auth.filter.jwt;

import com.msa.common.jwt.JwtProvider;
import com.msa.common.jwt.JwtResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtValidator {
    private final JwtProvider jwtProvider;

    public Mono<JwtResult> validate(String token) {
        JwtResult result = jwtProvider.validate(token);
        return result.isValid() ? Mono.just(result) : Mono.empty();
    }
}