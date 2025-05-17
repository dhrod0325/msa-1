package com.msa.gateway.filter;

import com.msa.common.jwt.JwtException;
import com.msa.common.jwt.JwtProvider;
import com.msa.common.jwt.JwtResult;
import com.msa.common.kafka.publisher.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtGatewayFilter implements GlobalFilter, Ordered {
    private final JwtProvider jwtProvider;

    private final KafkaEventPublisher kafkaEventPublisher;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        Map<String, Object> param = new HashMap<>();
        param.put("path", path);

        kafkaEventPublisher.publish("gateway", "filter", param);

        if (isExcludedPath(path)) {
            return chain.filter(exchange);
        }

        String rawToken = extractToken(exchange);

        if (rawToken == null || !rawToken.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing or invalid Authorization header");
        }

        String token = rawToken.substring(7);

        try {
            JwtResult claims = jwtProvider.validate(token);
            String userId = claims.getUserId();
            String roles = claims.getRoles();

            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(builder -> builder
                            .header("X-User-Id", userId)
                            .header("X-User-Roles", roles)
                    ).build();

            log.info("인증 완료: userId={}, roles={}, URI={}", userId, roles, exchange.getRequest().getURI());

            return chain.filter(mutatedExchange);
        } catch (JwtException e) {
            log.warn("JWT 인증 실패: {}", e.getMessage());
            return unauthorized(exchange, "Invalid JWT token");
        }
    }

    private String extractToken(ServerWebExchange exchange) {
        return exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

        log.warn("요청 거부: {}", message);

        return exchange.getResponse().setComplete();
    }

    private boolean isExcludedPath(String path) {
        return path.startsWith("/auth/login")
                || path.startsWith("/auth/signup")
                || path.startsWith("/auth/refresh");
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
