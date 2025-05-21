package com.msa.auth.oauth2;

import com.msa.auth.entity.OAuthUser;
import com.msa.auth.entity.User;
import com.msa.auth.repository.OauthUserRepository;
import com.msa.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OAuth2Service {
    private final OAuth2ProviderFactory providerFactory;

    private final OauthUserRepository oauthUserRepository;
    private final UserRepository userRepository;

    public Mono<OAuthUser> exchangeCodeForUser(String provider, String code) {
        return providerFactory.getProvider(provider)
                .exchange(code)
                .flatMap(this::saveOrGetUser);
    }

    private Mono<OAuthUser> saveOrGetUser(OAuthUser oauthUser) {
        return oauthUserRepository
                .findByProviderAndProviderUserId(
                        oauthUser.getProvider(),
                        oauthUser.getProviderUserId())
                .switchIfEmpty(oauthUserRepository.save(oauthUser));
    }

    public Mono<User> findUserByOAuthUserId(String providerUserId) {
        return userRepository.findById(Long.valueOf(providerUserId));
    }
}
