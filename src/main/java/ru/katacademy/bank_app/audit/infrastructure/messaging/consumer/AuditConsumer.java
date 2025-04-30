package ru.katacademy.bank_app.audit.infrastructure.messaging.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.katacademy.bank_app.account.domain.entity.AuditEntry;
import ru.katacademy.bank_app.audit.domain.repository.AuditRepository;

import java.time.LocalDateTime;

/**
 * Компонент для обработки аудиторских событий из Kafka.
 * <p>
 * Слушает события регистрации пользователей и переводов,
 * сохраняя их в аудит-лог.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuditConsumer {

    private final AuditRepository auditRepository;

    /**
     * Обрабатывает событие регистрации пользователя.
     *
     * @param message сообщение из Kafka-топика user-register-event
     */
    @KafkaListener(topics = "user-register-event", groupId = "audit")
    public void consumeUserRegister(String message) {
        log.info(message);

        final AuditEntry auditEntry = new AuditEntry(
                0L, // Временно, потом нужно парсить из получаемого параметра в методе
                "REGISTRATION",
                message,
                LocalDateTime.now(),
                "SUCCESS"
        );

        auditRepository.save(auditEntry);
    }

    /**
     * Обрабатывает событие успешного перевода.
     *
     * @param message сообщение из Kafka-топика transfer-completed-events
     */
    @KafkaListener(topics = "transfer-completed-events", groupId = "audit")
    public void consumeTransferCompleted(String message) {
        log.info(message);

        final AuditEntry auditEntry = new AuditEntry(
                0L, // Временно, потом нужно парсить из получаемого параметра в методе
                "TRANSFER",
                message,
                LocalDateTime.now(),
                "SUCCESS"
        );

        auditRepository.save(auditEntry);
    }
}
