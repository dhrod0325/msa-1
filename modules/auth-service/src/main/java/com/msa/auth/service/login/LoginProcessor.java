package com.msa.auth.service.login;

import com.msa.auth.dto.LoginRequest;
import com.msa.auth.entity.User;
import com.msa.auth.exception.UnauthorizedException;
import com.msa.auth.repository.UserRepository;
import com.msa.auth.service.login.strategy.LoginStrategy;
import com.msa.auth.service.login.token.TokenGenerator;
import com.msa.common.jwt.AuthTokenResponse;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginProcessor {
    private final List<LoginStrategy> strategies;
    private final TokenGenerator tokenGenerator;
    private final UserRepository userRepository;

    public AuthTokenResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("사용자를 찾을 수 없습니다."));

        applyStrategies(user, request); // 로그인 전략 수행

        // 사용자 상태 업데이트
        user.setLoginFailCount(0);
        user.setAccountLockedUntil(null);
        user.setLastLoginDate(LocalDateTime.now());

        userRepository.save(user);

        return tokenGenerator.generate(user);
    }

    private void applyStrategies(User user, LoginRequest request) {
        for (LoginStrategy strategy : strategies) {
            strategy.apply(user, request); // 동기 메서드로 가정
        }
    }
}
