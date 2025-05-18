package com.msa.auth.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class AuthListener {
    @KafkaListener(topics = "gateway.filter", groupId = "my-group")
    public void listen(String message) {
        System.out.println("Received Kafka message: " + message);
    }
}
