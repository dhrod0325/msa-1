package com.msa.auth.service.oauth2;

import com.msa.auth.entity.OAuthUser;
import reactor.core.publisher.Mono;

public interface OAuth2ProviderService {
    String providerName();

    Mono<OAuthUser> exchange(String code);
}