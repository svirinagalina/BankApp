package ru.katacademy.bank_shared.kafka;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.katacademy.bank.events.password.v1.PasswordChangedEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Сервис-производитель Kafka для публикации событий смены пароля пользователя.
 * <p>
 * Создает события типа {@link PasswordChangedEvent} и отправляет их в Kafka-топик {@code password-events}.
 * События сериализуются в Avro-формате согласно Avro-схеме, определенной в bank-shared.
 * author: Krasirskii Dmitrii
 * date: 14.01.2026
 */
@Service
public class KafkaAvroProducer {

    private final KafkaTemplate<String, PasswordChangedEvent> kafkaTemplate;

    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "KafkaTemplate is thread-safe and managed by Spring container"
    )
    public KafkaAvroProducer(KafkaTemplate<String, PasswordChangedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Публикует событие смены пароля пользователя в Kafka.
     * <p>
     * @param userId идентификатор пользователя, для которого произошло событие смены пароля
     */
    public void sendPasswordChangedEvent(String userId) {
        PasswordChangedEvent event = PasswordChangedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setUserId(userId)
                .setEventType("PASSWORD_CHANGED")
                .setOccurredAt(Instant.now().toEpochMilli())
                .setSource("user-settings-service")
                .build();

        kafkaTemplate.send("password-events", userId, event);
    }
}