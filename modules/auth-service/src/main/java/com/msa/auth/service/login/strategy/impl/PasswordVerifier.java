package com.msa.auth.service.login.strategy.impl;

import com.msa.auth.dto.LoginRequest;
import com.msa.auth.entity.User;
import com.msa.auth.exception.UnauthorizedException;
import com.msa.auth.repository.UserRepository;
import com.msa.auth.service.login.strategy.LoginStrategy;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordVerifier implements LoginStrategy {
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;

    @Override
    public void apply(User user, LoginRequest request) {
        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            int fail = user.getLoginFailCount() + 1;
            user.setLoginFailCount(fail);

            if (fail >= 5) {
                user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(30));
            }

            // 실패 상태 저장
            userRepository.save(user);

            throw new UnauthorizedException("비밀번호가 일치하지 않습니다.");
        }
        // 일치하면 아무 것도 하지 않음
    }
}
