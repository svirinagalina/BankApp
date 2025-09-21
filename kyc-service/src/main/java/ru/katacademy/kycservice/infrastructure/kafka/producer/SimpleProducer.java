package ru.katacademy.kycservice.infrastructure.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class SimpleProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic =  "kyc-events";

    public SimpleProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String key, String message) {
        kafkaTemplate.send(topic, key, message);
    }
}
