package ru.katacademy.bank_app.audit.application.service;

import org.springframework.stereotype.Service;
import ru.katacademy.bank_app.account.domain.entity.Account;
import ru.katacademy.bank_app.audit.domain.entity.AuditEntry;
import ru.katacademy.bank_app.audit.domain.repository.AuditRepository;
import ru.katacademy.bank_shared.valueobject.AccountNumber;
import ru.katacademy.bank_shared.valueobject.Money;

import java.time.LocalDateTime;
import java.util.List;

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
     * Метод для логирования операций перевода.
     *
     * @param userId        идентификатор пользователя
     * @param accountNumberFrom   номер счета отправителя
     * @param accountNumberTo     номер счета получателя
     * @param amount        сумма перевода
     * @param status        статус операции
     */
    public void logTransfer(Long userId, AccountNumber accountNumberFrom, AccountNumber accountNumberTo, Money amount, String status) {
        final String action = "Transfer";
        final String details = "Перевод со счета " + accountNumberFrom + " на счет " + accountNumberTo + " на сумму " + amount;

        logAction(userId, action, details, status);
    }

    /**
     * Метод для логирования действий пользователя
     *
     * @param userId        идентификатор пользователя
     * @param action        действие пользователя
     * @param details       детали операции
     * @param status        статус операции
     */
    public void logAction(Long userId, String action, String details, String status) {
        final AuditEntry entry = new AuditEntry(
                userId,
                action,
                details,
                LocalDateTime.now(),
                status
        );
        auditRepository.save(entry);
    }

    public List<AuditEntry> getAllAudits() {
        return auditRepository.getAllAudits();
    }

    public List<AuditEntry> getAllAuditsByType(String eventType) {
        return auditRepository.getAllAuditsByType(eventType);
    }
}
