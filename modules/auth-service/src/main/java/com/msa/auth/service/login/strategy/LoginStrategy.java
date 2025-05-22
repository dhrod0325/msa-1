package com.msa.auth.service.login.strategy;

import com.msa.auth.dto.LoginRequest;
import com.msa.auth.entity.User;

public interface LoginStrategy {
    void apply(User user, LoginRequest request);
}
