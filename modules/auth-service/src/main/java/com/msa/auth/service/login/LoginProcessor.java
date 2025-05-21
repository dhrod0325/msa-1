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
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LoginProcessor {
    //LoginStrategy 는 구현체 리스트가 자동 주입됨
    private final List<LoginStrategy> strategies;

    private final TokenGenerator tokenGenerator;
    private final UserRepository userRepository;

    public Mono<AuthTokenResponse> login(LoginRequest request) {
        return userRepository.findByUsername(request.getUsername())
                .switchIfEmpty(Mono.error(new UnauthorizedException("사용자를 찾을 수 없습니다.")))
                .flatMap(user -> applyStrategies(user, request))
                .flatMap(user -> {
                    user.setLoginFailCount(0);
                    user.setAccountLockedUntil(null);
                    user.setLastLoginDate(LocalDateTime.now());
                    return userRepository.save(user);
                })
                .flatMap(tokenGenerator::generate);
    }

    private Mono<User> applyStrategies(User user, LoginRequest request) {
        Mono<User> result = Mono.just(user);

        for (LoginStrategy strategy : strategies) {
            result = result.flatMap(u -> strategy.apply(u, request));
        }

        return result;
    }
}