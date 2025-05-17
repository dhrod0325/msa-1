package com.msa.common.kafka.provider;

import com.msa.common.kafka.repository.EventDefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class KafkaDbEventProvider implements KafkaEventProvider {
    private final EventDefinitionRepository eventDefinitionRepository;

    @Override
    public Mono<Boolean> isEnabled(String service, String eventCode) {
        return eventDefinitionRepository.existsByServiceAndEventCodeAndEnabledTrue(service, eventCode);
    }
}
