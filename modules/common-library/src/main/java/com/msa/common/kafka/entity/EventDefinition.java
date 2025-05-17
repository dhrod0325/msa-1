package com.msa.common.kafka.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("event_definitions")
public class EventDefinition {
    @Id
    private Long id;

    private String service;

    private String eventCode;

    private String description;

    private Boolean enabled;

    private LocalDateTime createdAt;
}
