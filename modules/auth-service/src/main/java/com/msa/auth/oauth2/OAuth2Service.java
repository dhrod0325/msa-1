package com.msa.auth.oauth2;

import com.msa.auth.entity.OAuthUser;
import com.msa.auth.entity.User;
import com.msa.auth.repository.OauthUserRepository;
import com.msa.auth.repository.UserRepository;
import com.msa.common.kafka.events.SiteUserCreatedEvent;
import com.msa.common.kafka.publisher.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2Service {

    private final OAuth2ProviderFactory providerFactory;

    private final OauthUserRepository oauthUserRepository;
    private final UserRepository userRepository;

    private final KafkaEventPublisher kafkaEventPublisher;

    public OAuthUser exchangeCodeForUserBlocking(String provider, String code) {
        OAuth2ProviderService providerImpl = providerFactory.getProvider(provider);
        OAuthUser user = providerImpl.exchangeBlocking(code);
        return saveOrGetUserBlocking(user);
    }

    private OAuthUser saveOrGetUserBlocking(OAuthUser oauthUser) {
        return oauthUserRepository
                .findByProviderAndProviderUserId(oauthUser.getProvider(), oauthUser.getProviderUserId())
                .orElseGet(() -> oauthUserRepository.save(oauthUser));
    }

    public User findUserByOAuthUserIdBlocking(String providerUserId) {
        return userRepository.findById(Long.valueOf(providerUserId)).orElse(null);
    }

    public void publishSignupEventBlocking(OAuthUser oAuthUser) {
        SiteUserCreatedEvent event = SiteUserCreatedEvent.builder()
                .provider(oAuthUser.getProvider())
                .providerUserId(oAuthUser.getProviderUserId())
                .username(oAuthUser.getNickname())
                .email(oAuthUser.getEmail())
                .role("USER")
                .build();

        kafkaEventPublisher.publish("site-user", "events", event);
        log.info("가입 이벤트 발행됨: {}", event);
    }
}
