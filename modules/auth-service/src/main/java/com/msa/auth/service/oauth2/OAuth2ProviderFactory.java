package com.msa.auth.service.oauth2;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2ProviderFactory {

    private final List<OAuth2ProviderService> providers;

    public OAuth2ProviderService getProvider(String name) {
        return providers.stream()
                .filter(p -> p.providerName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 provider: " + name));
    }
}
