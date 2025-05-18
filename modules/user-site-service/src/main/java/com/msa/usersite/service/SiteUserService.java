package com.msa.usersite.service;

import com.msa.common.kafka.events.SiteUserCreatedEvent;
import com.msa.common.kafka.publisher.KafkaEventPublisher;
import com.msa.usersite.entity.SiteUserEntity;
import com.msa.usersite.repository.SiteUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SiteUserService {
    private final SiteUserRepository repository;

    private final KafkaEventPublisher kafkaEventPublisher;

    public Mono<Void> createUser(SiteUserEntity user, String siteCode) {
        return repository.save(user)
                .doOnSuccess(saved -> {
                    SiteUserCreatedEvent event = SiteUserCreatedEvent.builder()
                            .siteCode(siteCode)
                            .username(user.getUsername())
                            .encodedPassword(user.getPassword())
                            .role(user.getRole())
                            .build();

                    kafkaEventPublisher.publish("site-user", "events", event);
                }).then();
    }
}
