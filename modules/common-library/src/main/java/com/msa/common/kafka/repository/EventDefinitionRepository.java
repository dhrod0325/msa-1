package com.msa.common.kafka.repository;

import com.msa.common.kafka.entity.EventDefinition;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventDefinitionRepository extends JpaRepository<EventDefinition, Long> {

    boolean existsByServiceAndEventCodeAndEnabledTrue(String service, String eventCode);

    List<EventDefinition> findAllByServiceAndEnabledTrue(String service);
}
