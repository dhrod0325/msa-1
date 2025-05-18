package com.msa.common.kafka.events;

import java.time.LocalDateTime;

public interface BaseEvent {
    String getEventType();

    LocalDateTime getCreatedAt();
}