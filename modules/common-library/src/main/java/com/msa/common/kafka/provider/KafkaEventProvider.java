package com.msa.common.kafka.provider;

public interface KafkaEventProvider {
    boolean isEnabled(String service, String eventCode);
}
