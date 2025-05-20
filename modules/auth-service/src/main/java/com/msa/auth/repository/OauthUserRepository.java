package com.msa.auth.repository;

import com.msa.auth.entity.OAuthUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface OauthUserRepository extends ReactiveCrudRepository<OAuthUser, Long> {
    Mono<OAuthUser> findByProviderAndProviderUserId(String provider, String providerUserId);
}