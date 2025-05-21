package com.msa.auth.service.login.strategy.impl;

import com.msa.auth.dto.LoginRequest;
import com.msa.auth.entity.User;
import com.msa.auth.exception.UnauthorizedException;
import com.msa.auth.service.login.strategy.LoginStrategy;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AccountLockChecker implements LoginStrategy {
    @Override
    public Mono<User> apply(User user, LoginRequest request) {
        if (user.getAccountLockedUntil() != null && user.getAccountLockedUntil().isAfter(LocalDateTime.now())) {
            return Mono.error(new UnauthorizedException("계정이 잠겨 있습니다."));
        }
        return Mono.just(user);
    }
}