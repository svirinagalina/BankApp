package ru.katacademy.bank_app.accountservice.infrastructure.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaHealthIndicator implements HealthIndicator {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaHealthIndicator(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public Health health() {
        try {
            kafkaTemplate.metrics(); // проверка живости Kafka
            return Health.up().withDetail("Kafka", "Available").build();
        } catch (Exception e) {
            return Health.down().withDetail("Kafka", "Unavailable").withException(e).build();
        }
    }
}

