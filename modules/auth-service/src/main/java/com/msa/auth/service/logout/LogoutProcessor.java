package com.msa.auth.service.logout;

import com.msa.auth.store.RefreshTokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogoutProcessor {
    private final RefreshTokenStore refreshTokenStore;

    public void logout(String userId, String accessToken) {
        refreshTokenStore.delete(userId);
        refreshTokenStore.blacklistAccessToken(accessToken);
        refreshTokenStore.deleteSession(userId);
    }
}
