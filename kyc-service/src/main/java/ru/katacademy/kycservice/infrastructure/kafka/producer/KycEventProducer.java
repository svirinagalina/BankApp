package ru.katacademy.kycservice.infrastructure.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KycEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic =  "kyc-events";

    public KycEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String key, String message) {
        kafkaTemplate.send(topic, key, message);
    }
}
