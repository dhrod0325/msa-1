package com.msa.common.kafka.provider;

import reactor.core.publisher.Mono;

public interface KafkaEventProvider {
    Mono<Boolean> isEnabled(String service, String eventCode);
}
