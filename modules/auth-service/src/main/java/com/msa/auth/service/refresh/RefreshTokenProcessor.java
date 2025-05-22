package com.msa.auth.service.refresh;

import com.msa.auth.exception.UnauthorizedException;
import com.msa.auth.store.RefreshTokenStore;
import com.msa.common.jwt.AuthTokenResponse;
import com.msa.common.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenProcessor {
    private final RefreshTokenStore refreshTokenStore;
    private final JwtProvider jwtProvider;

    public AuthTokenResponse refresh(String userId, String refreshToken) {
        boolean isValid = refreshTokenStore.validate(userId, refreshToken);

        if (!isValid) {
            throw new UnauthorizedException();
        }

        String access = jwtProvider.generateAccessToken(userId, "ROLE_USER");
        return new AuthTokenResponse(access, refreshToken);
    }
}
