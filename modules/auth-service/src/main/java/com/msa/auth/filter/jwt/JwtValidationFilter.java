package com.msa.auth.filter.jwt;

import com.msa.common.jwt.JwtResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtValidationFilter implements WebFilter {

    private final TokenExtractor tokenExtractor;
    private final AccessTokenBlacklistValidator blacklistValidator;
    private final JwtValidator jwtValidator;
    private final SessionIdValidator sessionIdValidator;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = tokenExtractor.extract(exchange);
        if (token == null) {
            return chain.filter(exchange);
        }

        return blacklistValidator.isBlacklisted(token)
                .flatMap(blacklisted -> {
                    if (blacklisted) {
                        return unauthorized(exchange);
                    }

                    return jwtValidator.validate(token);
                })
                .switchIfEmpty(unauthorized(exchange))
                .cast(JwtResult.class)
                .flatMap(jwtResult -> {
                    String userId = jwtResult.getUserId();
                    String sessionId = jwtResult.getSessionId();
                    if (userId == null || sessionId == null) {
                        return unauthorized(exchange);
                    }

                    return sessionIdValidator.isValid(userId, sessionId)
                            .flatMap(valid -> valid ? chain.filter(exchange) : unauthorized(exchange));
                })
                .onErrorResume(e -> unauthorized(exchange));
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
