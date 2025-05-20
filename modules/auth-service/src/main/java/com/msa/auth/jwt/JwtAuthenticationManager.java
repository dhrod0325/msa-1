package com.msa.auth.jwt;

import com.msa.common.jwt.JwtProvider;
import com.msa.common.jwt.JwtResult;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtProvider jwtProvider;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        JwtResult result = jwtProvider.validate(token);

        if (!result.isValid()) {
            return Mono.empty();
        }

        String userId = result.getUserId();
        String role = result.getRoles();

        return Mono.just(new UsernamePasswordAuthenticationToken(userId, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))));
    }
}