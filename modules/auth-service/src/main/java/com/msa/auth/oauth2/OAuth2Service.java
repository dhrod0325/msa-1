package com.msa.auth.oauth2;

import com.msa.auth.entity.OAuthUser;
import com.msa.auth.entity.User;
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
    private final UserRepository userRepository;

    private final KafkaEventPublisher kafkaEventPublisher;

    public User findUserByOAuthUserId(String providerUserId) {
        return userRepository.findById(Long.valueOf(providerUserId)).orElse(null);
    }

    public void publishSignupEvent(OAuthUser oAuthUser) {
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
