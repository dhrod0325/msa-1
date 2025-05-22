package com.msa.auth.oauth2;

import com.msa.auth.entity.OAuthUser;

public interface OAuth2ProviderService {
    String providerName();

    OAuthUser exchange(String code);
}