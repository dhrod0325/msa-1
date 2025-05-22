package com.msa.auth.filter.jwt;

import com.msa.common.jwt.JwtResult;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtValidationFilter implements Filter {

    private final TokenExtractor tokenExtractor;
    private final AccessTokenBlacklistValidator blacklistValidator;
    private final JwtValidator jwtValidator;
    private final SessionIdValidator sessionIdValidator;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String token = tokenExtractor.extract(request);
        if (token == null) {
            chain.doFilter(request, response);
            return;
        }

        try {
            if (blacklistValidator.isBlacklisted(token)) {
                unauthorized(response);
                return;
            }

            JwtResult jwtResult = jwtValidator.validate(token);
            if (jwtResult == null ||
                    jwtResult.getUserId() == null ||
                    jwtResult.getSessionId() == null) {
                unauthorized(response);
                return;
            }

            boolean validSession = sessionIdValidator.isValid(jwtResult.getUserId(), jwtResult.getSessionId());
            if (!validSession) {
                unauthorized(response);
                return;
            }

            // 통과
            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error("JWT 검증 중 예외 발생", e);
            unauthorized(response);
        }
    }

    private void unauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Unauthorized");
    }
}
