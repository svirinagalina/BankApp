package ru.katacademy.bank_app.audit.domain.repository;

import ru.katacademy.bank_app.audit.domain.entity.AuditEntry;

/**
 * Интерфейс определяет контракт для сохранения {@link AuditEntry} в базу данных.
 * <p>
 * Автор: Maxim4212
 * Дата: 2025-05-10
 */
public interface AuditRepository {

    /**
     * Сохраняет указанную запись аудита.
     *
     * @param auditEntry объект {@link AuditEntry}, содержащий данные события
     */
    void save(AuditEntry auditEntry);
}
