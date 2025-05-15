package ru.katacademy.bank_app.audit.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.katacademy.bank_app.audit.domain.entity.AuditEntry;
import ru.katacademy.bank_app.audit.domain.repository.AuditRepository;

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

    /**
     * Записывает событие в аудит и логирует результат операции.
     * В случае возникновения ошибки при сохранении события
     * генерируется исключение {@link AuditServiceException}.
     * @param entry объект типа {@link AuditEntry}, который содержит информацию о событии для записи в аудит.
     * @throws AuditServiceException если произошла ошибка при сохранении события в аудит.
     */
    public void record(AuditEntry entry) {
        log.debug("Начало записи события в аудит: {}", entry);
        try {
            auditRepository.save(entry);
            log.info("Событие успешно записано в аудит: {}", entry);
        } catch (Exception e) {
            log.error("Ошибка при записи события в аудит", e);
            throw new AuditServiceException("Не удалось записать событие в аудит", e);
        }
    }
}
