package com.msa.common.kafka.events;

import java.time.Instant;

public interface BaseEvent {
    String getEventType();

    Instant getCreatedAt();
}