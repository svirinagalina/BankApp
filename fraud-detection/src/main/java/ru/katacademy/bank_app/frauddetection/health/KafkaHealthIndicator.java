package ru.katacademy.bank_app.frauddetection.health;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * HealthIndicator для Kafka, используемый Spring Boot Actuator.
 * <p>
 * Этот компонент проверяет доступность кластера Kafka с помощью {@link AdminClient}.
 * В случае успешного соединения возвращает статус {@code UP}, иначе — {@code DOWN} с описанием ошибки.
 * </p>
 *
 * <p>
 * Компонент активен только вне профиля {@code test} (см. аннотацию {@link Profile}).
 * </p>
 */
@Component
@Profile("!test")
public class KafkaHealthIndicator implements HealthIndicator {

    /**
     * Конфигурация клиента Kafka, аналогичная {@code kafkaAdmin.configurationProperties}.
     * Используется для создания {@link AdminClient}.
     */
    private final Map<String, Object> kafkaConfig;

    /**
     * Конструктор, принимающий конфигурацию Kafka.
     *
     * @param kafkaConfig карта конфигурационных свойств Kafka, например:
     *                    <ul>
     *                      <li>{@link AdminClientConfig#BOOTSTRAP_SERVERS_CONFIG}</li>
     *                      <li>{@link AdminClientConfig#CLIENT_ID_CONFIG}</li>
     *                    </ul>
     */
    public KafkaHealthIndicator(@Value("#{kafkaAdmin.configurationProperties}") Map<String, Object> kafkaConfig) {
        // Копируем карту, чтобы избежать проблем с мутабельностью
        this.kafkaConfig = new HashMap<>(kafkaConfig);
    }

    /**
     * Проверяет доступность кластера Kafka.
     *
     * <p>Метод создаёт {@link AdminClient} и выполняет вызов {@code describeCluster().nodes()}
     * с таймаутом 5 секунд.</p>
     *
     * <p>Обрабатываются исключения следующим образом:
     * <ul>
     *     <li>{@link InterruptedException} — восстанавливается флаг прерывания и возвращается статус {@code DOWN} с описанием.</li>
     *     <li>Все остальные исключения (включая {@link RuntimeException}) — возвращается статус {@code DOWN} с описанием ошибки.</li>
     * </ul>
     * </p>
     *
     * @return объект {@link Health} со статусом {@code UP}, если Kafka доступен,
     *         или {@code DOWN} с описанием ошибки при недоступности.
     */
    @Override
    public Health health() {
        try (AdminClient adminClient = createAdminClient()) {
            adminClient.describeCluster().nodes().get(5, TimeUnit.SECONDS);
            return Health.up().withDetail("Kafka", "Available").build();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Health.down()
                    .withDetail("Kafka", "Unavailable")
                    .withDetail("error", "Interrupted: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            // Включая RuntimeException
            return Health.down()
                    .withDetail("Kafka", "Unavailable")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }


    /**
     * Создаёт экземпляр {@link AdminClient} на основе текущей конфигурации.
     *
     * <p>Метод помечен как {@code protected} для возможности переопределения в тестах,
     * например, с целью подмены на мок-объект.</p>
     *
     * @return созданный {@link AdminClient}
     */
    protected AdminClient createAdminClient() {
        return AdminClient.create(kafkaConfig);
    }
}
