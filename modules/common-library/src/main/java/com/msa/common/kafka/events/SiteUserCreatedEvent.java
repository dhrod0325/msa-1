package com.msa.common.kafka.events;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteUserCreatedEvent implements BaseEvent {
    private String eventType = "SITE_USER_CREATED";
    private LocalDateTime createdAt = LocalDateTime.now();

    private String siteCode;
    private String username;
    private String encodedPassword;
    private String role;
    private String email;

    private String provider;
    private String providerUserId;
}