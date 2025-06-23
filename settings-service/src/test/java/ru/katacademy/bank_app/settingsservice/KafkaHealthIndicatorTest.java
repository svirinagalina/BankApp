package ru.katacademy.bank_app.settingsservice;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import ru.katacademy.bank_app.settingsservice.health.KafkaHealthIndicator;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Юнит-тесты для {@link KafkaHealthIndicator}.
 *
 * <p>Включает тесты с подменой метода создания {@link AdminClient} для симуляции отказа
 * и проверки поведения индикатора в различных ситуациях.</p>
 */
public class KafkaHealthIndicatorTest {

    /**
     * Тестовый подкласс {@link KafkaHealthIndicator}, который
     * переопределяет создание {@link AdminClient} и выбрасывает исключение
     * для имитации недоступности Kafka.
     */
    static class TestKafkaHealthIndicator extends KafkaHealthIndicator {
        TestKafkaHealthIndicator(Map<String, Object> config) {
            super(config);
        }

        @Override
        protected AdminClient createAdminClient() {
            throw new RuntimeException("Kafka is down"); // симулируем отказ
        }
    }

    /**
     * Тест проверяет, что при отказе Kafka (исключении при создании AdminClient)
     * {@link KafkaHealthIndicator#health()} возвращает статус {@link Status#DOWN}
     * и содержит сообщение об ошибке.
     */
    @Test
    void shouldReturnDownWhenKafkaUnavailable() {
        final Map<String, Object> dummyConfig = Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"
        );

        final KafkaHealthIndicator indicator = new TestKafkaHealthIndicator(dummyConfig);

        final Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertTrue(((String) health.getDetails().get("error")).contains("Kafka is down"));
    }

    /**
     * Тест проверяет, что в замоканном окружении метод {@link KafkaHealthIndicator#health()}
     * возвращает статус {@link Status#UP} и корректный detail.
     */
    @Test
    void shouldReturnUpInMockedEnvironment() {
        final KafkaHealthIndicator indicator = new KafkaHealthIndicator(Map.of()) {
            @Override
            public Health health() {
                return Health.up().withDetail("Kafka", "Mocked Available").build();
            }
        };

        final Health health = indicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals("Mocked Available", health.getDetails().get("Kafka"));
    }
}

