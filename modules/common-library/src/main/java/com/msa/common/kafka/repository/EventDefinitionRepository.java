package com.msa.common.kafka.repository;

import com.msa.common.kafka.entity.EventDefinition;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface EventDefinitionRepository extends ReactiveCrudRepository<EventDefinition, Long> {

    Mono<Boolean> existsByServiceAndEventCodeAndEnabledTrue(String service, String eventCode);

    Flux<EventDefinition> findAllByServiceAndEnabledTrue(String service);
}
