package com.msa.auth.service.login.strategy;

import com.msa.auth.dto.LoginRequest;
import com.msa.auth.entity.User;
import reactor.core.publisher.Mono;

public interface LoginStrategy {
    Mono<User> apply(User user, LoginRequest request);
}