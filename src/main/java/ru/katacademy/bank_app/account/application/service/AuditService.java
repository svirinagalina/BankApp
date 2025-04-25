package ru.katacademy.bank_app.account.application.service;

import org.springframework.stereotype.Service;
import ru.katacademy.bank_app.account.domain.entity.AuditEntry;
import ru.katacademy.bank_app.account.domain.repository.AuditRepository;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Сервис для логирования действий пользователей и финансовых операций.
 *
 * Предоставляет методы для записи в журнал аудита различных событий системы.
 */
@Service
public class AuditService {
    private final AuditRepository auditRepository;

    /**
     * Конструктор сервиса аудита.
     *
     * @param auditRepository репозиторий для работы с записями аудита
     */
    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    /**
     * Логирует произвольное действие пользователя.
     *
     * @param userId  идентификатор пользователя
     * @param action  тип действия (например, "LOGIN", "TRANSFER")
     * @param details детальное описание действия
     * Timestamp - время действий пользователя
     * @param status  статус выполнения ("SUCCESS", "FAILED" и т.д.)
     */
    public void logAction(Long userId, String action, String details, String status) {
        AuditEntry entry = new AuditEntry(
                userId,
                action,
                details,
                new Timestamp(System.currentTimeMillis()), // Создание timestamp
                status
        );
        auditRepository.save(entry);
    }

    /**
     * Метод для логирования операций перевода.
     *
     * @param userId        идентификатор пользователя
     * @param fromAccountId идентификатор счета-отправителя
     * @param toAccountId   идентификатор счета-получателя
     * @param amount        сумма перевода
     * @param status        статус операции
     */
    public void logTransfer(Long userId, Long fromAccountId, Long toAccountId,
                            BigDecimal amount, String status) {
        String details = String.format("Transfer from %s to %s amount %s", fromAccountId, toAccountId, amount);
        logAction(userId, "TRANSFER", details, status);
    }
}
