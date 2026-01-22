package ru.katacademy.bank_app.audit.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.katacademy.bank_shared.event.notification.PasswordChangedEvent;
import ru.katacademy.bank_app.audit.domain.entity.AuditEntry;
import ru.katacademy.bank_app.audit.persistence.entity.AuditEvent;
import ru.katacademy.bank_app.audit.persistence.repository.AuditEventRepository;
import ru.katacademy.bank_app.audit.domain.repository.AuditRepository;

import java.time.Instant;

/**
 * Сервис для записи событий в аудит.
 * <p>
 * Класс отвечает за обработку логики записи событий в систему аудита.
 * Он взаимодействует с репозиторием {@link AuditRepository} для сохранения
 * данных и логирует результаты операции.
 * В случае ошибки при сохранении события выбрасывается кастомное исключение {@link AuditServiceException}.
 * <p>
 * Поля:
 * - auditRepository - Репозиторий для работы с данными аудита.
 * </p>
 * * Автор: Maxim4212
 * * Дата: 2025-05-11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditRepository auditRepository;
    private final AuditEventRepository repository;

    /**
     * Записывает событие в аудит и логирует результат операции.
     * В случае возникновения ошибки при сохранении события
     * генерируется исключение {@link AuditServiceException}.
     * @param entry объект типа {@link AuditEntry}, который содержит информацию о событии для записи в аудит.
     * @throws AuditServiceException если произошла ошибка при сохранении события в аудит.
     */
    public void recordAuditEntry(AuditEntry entry) {
        log.debug("Начало записи события в аудит: {}", entry);
        try {
            auditRepository.save(entry);
            log.info("Событие успешно записано в аудит: {}", entry);
        } catch (Exception e) {
            log.error("Ошибка при записи события в аудит", e);
            throw new AuditServiceException("Не удалось записать событие в аудит", e);
        }
    }

    public void savePasswordChangeEvent(PasswordChangedEvent event) {
        final AuditEvent audit = AuditEvent.builder()
                .userId(Long.valueOf(event.getUserId()))
                .eventType(event.getEventType())
                .occurredAt(Instant.ofEpochMilli(event.getOccurredAt()))
                .source(event.getSource())
                .build();

        repository.save(audit);
    }
}
