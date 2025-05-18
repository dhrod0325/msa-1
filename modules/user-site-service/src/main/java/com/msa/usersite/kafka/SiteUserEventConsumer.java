package com.msa.usersite.kafka;

import com.msa.common.kafka.events.SiteUserCreatedEvent;
import com.msa.usersite.entity.SiteEntity;
import com.msa.usersite.entity.SiteUserEntity;
import com.msa.usersite.repository.SiteRepository;
import com.msa.usersite.repository.SiteUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class SiteUserEventConsumer {

    private final SiteUserRepository userRepository;
    private final SiteRepository siteRepository;

    @KafkaListener(topics = "site-user.events", groupId = "my-group")
    public void consume(SiteUserCreatedEvent event) {
        SiteEntity siteEntity = new SiteEntity();
        siteEntity.setSiteCode(event.getSiteCode());

        siteRepository.findBySiteCode(event.getSiteCode())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("존재하지 않는 siteCode: " + event.getSiteCode())))
                .flatMap(site ->
                        userRepository.findBySiteIdAndUsername(site.getId(), event.getUsername())
                                .flatMap(existing -> {
                                    existing.setPassword(event.getEncodedPassword());
                                    existing.setRole(event.getRole());
                                    return userRepository.save(existing);
                                })
                                .switchIfEmpty(
                                        userRepository.save(SiteUserEntity.builder()
                                                .siteId(site.getId())
                                                .username(event.getUsername())
                                                .password(event.getEncodedPassword())
                                                .role(event.getRole())
                                                .build())
                                )
                )
                .subscribe();
    }
}