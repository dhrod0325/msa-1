package com.msa.auth.filter.jwt;

import com.msa.auth.store.RefreshTokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionIdValidator {

    private final RefreshTokenStore refreshTokenStore;

    public boolean isValid(String userId, String sessionId) {
        String savedSessionId = refreshTokenStore.getSessionId(userId);
        return sessionId.equals(savedSessionId);
    }
}
