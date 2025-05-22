package com.msa.common.kafka.publisher;

import com.msa.common.kafka.provider.KafkaEventProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaEventProvider kafkaEventProvider;

    public void publish(String service, String topic, Object event) {
        String topicName = service + "." + topic;

        if (!kafkaEventProvider.isEnabled(service, topic)) {
            log.warn("토픽 '{}'은 비활성화되어 메시지를 발행하지 않음", topic);
            return;
        }

        kafkaTemplate.send(topicName, event);
    }
}
