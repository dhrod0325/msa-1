package com.msa.common.kafka.provider;

import com.msa.common.kafka.repository.EventDefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaDbEventProvider implements KafkaEventProvider {

    private final EventDefinitionRepository eventDefinitionRepository;

    @Override
    public boolean isEnabled(String service, String eventCode) {
        return eventDefinitionRepository.existsByServiceAndEventCodeAndEnabledTrue(service, eventCode);
    }
}
