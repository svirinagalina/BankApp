package ru.katacademy.bank_app.audit.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.katacademy.bank_app.audit.application.service.AuditService;
import ru.katacademy.bank_app.audit.domain.entity.AuditEntry;
import ru.katacademy.bank_app.audit.persistence.mapper.AuditEntryMapper;

/**
 * Слушает определённые Kafka-топики, преобразует сообщения в {@link AuditEntry}
 * с помощью {@link AuditEntryMapper}, и передаёт их в {@link AuditService} для записи.
 * <p>
 * Поля:
 * - auditEntryMapper: преобразует события Kafka в AuditEntry
 * - auditService: записывает AuditEntry в лог и БД
 * <p>
 * Автор: Maxim4212
 * Дата: 2025-05-10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditConsumer {

    private final AuditService auditService;
    private final AuditEntryMapper auditEntryMapper;

    /**
     * Обрабатывает событие завершения перевода.
     *
     * @param message сообщение из Kafka в виде строки
     */
    @KafkaListener(topics = "transfer-completed-events", groupId = "audit")
    public void consumeTransferEvent(String message) {
        log.info("Получено событие перевода: {}", message);
        final AuditEntry entry = auditEntryMapper.fromTransferCompletedEvent(message);
        auditService.record(entry);
    }

    /**
     * Обрабатывает событие регистрации пользователя.
     *
     * @param message сообщение из Kafka в виде строки
     */
    @KafkaListener(topics = "user-register-events", groupId = "audit")
    public void consumeUserRegisterEvent(String message) {
        log.info("Получено событие регистрации пользователя: {}", message);
        final AuditEntry entry = auditEntryMapper.fromUserRegisterEvent(message);
        auditService.record(entry);
    }
}
