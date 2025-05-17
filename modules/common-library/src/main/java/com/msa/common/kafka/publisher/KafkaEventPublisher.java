package com.msa.common.kafka.publisher;

import com.msa.common.kafka.provider.KafkaEventProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final KafkaEventProvider kafkaEventProvider;

    public void publish(String service, String topic, Object event) {
        String topicName = service + "." + topic;

        kafkaEventProvider.isEnabled(service, topic)
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.fromRunnable(() -> log.warn("토픽 '{}'은 비활성화되어 메시지를 발행하지 않음", topic)))
                .flatMap(enabled -> {
                    kafkaTemplate.send(topicName, event);

                    return Mono.just(enabled);
                }).subscribe();
    }
}