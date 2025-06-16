package ru.katacademy.bank_app.accountservice.infrastructure.health;

import org.apache.kafka.clients.admin.AdminClient;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class KafkaHealthIndicator implements HealthIndicator {

    private final KafkaAdmin kafkaAdmin;

    public KafkaHealthIndicator(KafkaAdmin kafkaAdmin) {
        this.kafkaAdmin = kafkaAdmin;
    }

    @Override
    public Health health() {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            adminClient.describeCluster()
                    .nodes()
                    .get(5, TimeUnit.SECONDS);
            return Health.up().withDetail("message", "Kafka is available").build();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Health.down()
                    .withDetail("error", "Thread was interrupted: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", "Kafka is unavailable: " + e.getMessage())
                    .build();
        }
    }
}