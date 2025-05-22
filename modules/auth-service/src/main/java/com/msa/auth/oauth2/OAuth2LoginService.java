package com.msa.auth.oauth2;


import com.msa.auth.dto.AuthTokenResponse;
import com.msa.auth.entity.OAuthUser;
import com.msa.auth.entity.User;
import com.msa.auth.oauth2.exception.UserNotFoundException;
import com.msa.auth.store.RefreshTokenStore;
import com.msa.common.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class OAuth2LoginService {
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2ProviderFactory strategyFactory;
    private final OAuth2Service oAuth2Service;
    private final JwtProvider jwtProvider;
    private final RefreshTokenStore refreshTokenStore;

    /**
     * OAuth2 인증 코드로 사용자 정보 조회 후 JWT 토큰 발급
     */
    public AuthTokenResponse handleCallback(String provider, String code) {
        var strategy = strategyFactory.getProvider(provider);

        OAuthUser oAuthUser = strategy.exchange(code);

        if (oAuthUser == null) {
            throw new IllegalArgumentException("OAuth 사용자 정보를 가져올 수 없습니다.");
        }

        User user = oAuth2Service.findUserByOAuthUserId(oAuthUser.getProviderUserId());

        if (user == null) {
            oAuth2Service.publishSignupEvent(oAuthUser);
            throw new UserNotFoundException("사용자가 존재하지 않아 가입 이벤트를 발행했습니다.");
        }

        String accessToken = jwtProvider.generateAccessToken(user.getId().toString(), user.getRole());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId().toString(), user.getRole());

        refreshTokenStore.save(user.getId().toString(), refreshToken);

        return new AuthTokenResponse(accessToken, refreshToken);
    }

    public String getAuthorizationUri(String provider) {
        ClientRegistration registration = clientRegistrationRepository.findByRegistrationId(provider);

        if (registration == null) {
            throw new RuntimeException();
        }

        String baseAuthUri = switch (provider) {
            case "kakao" -> "https://kauth.kakao.com/oauth/authorize";
            case "naver" -> "https://nid.naver.com/oauth2.0/authorize";
            case "google" -> "https://accounts.google.com/o/oauth2/v2/auth";
            default -> registration.getProviderDetails().getAuthorizationUri();
        };

        return UriComponentsBuilder.fromUriString(baseAuthUri)
                .queryParam("response_type", "code")
                .queryParam("client_id", registration.getClientId())
                .queryParam("redirect_uri", registration.getRedirectUri())
                .queryParam("scope", String.join(",", registration.getScopes()))
                .build(true)
                .toUriString();
    }
}