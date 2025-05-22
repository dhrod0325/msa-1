package com.msa.auth.service.login.token;

import com.msa.auth.entity.User;
import com.msa.auth.store.RefreshTokenStore;
import com.msa.common.jwt.AuthTokenResponse;
import com.msa.common.jwt.JwtProvider;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenGenerator {
    private final JwtProvider jwtProvider;
    private final RefreshTokenStore refreshTokenStore;

    public AuthTokenResponse generate(User user) {
        String sessionId = UUID.randomUUID().toString();
        String userId = user.getId().toString();

        String access = jwtProvider.generateAccessToken(userId, user.getRole(), sessionId);
        String refresh = jwtProvider.generateRefreshToken(userId, user.getRole());

        refreshTokenStore.save(userId, refresh);           // 동기 저장
        refreshTokenStore.saveSession(userId, sessionId);  // 동기 저장

        return new AuthTokenResponse(access, refresh);
    }
}
