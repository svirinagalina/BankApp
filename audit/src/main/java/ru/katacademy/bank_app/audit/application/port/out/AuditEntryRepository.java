package ru.katacademy.bank_app.audit.application.port.out;

import ru.katacademy.bank_app.audit.domain.entity.AuditEntry;
import java.util.List;

/**
 * Порт выхода для работы с логами аудита.
 * Предоставляет методы сохранения и выборки {@link AuditEntry}.
 */
public interface AuditEntryRepository {

    /**
     * Сохраняет новую запись аудита.
     *
     * @param entry доменный объект {@link AuditEntry} для сохранения
     * @return сохранённая запись с заполненными полями (например, id, timestamp)
     */
    AuditEntry save(AuditEntry entry);

    /**
     * Возвращает все записи аудита.
     *
     * @return список всех {@link AuditEntry} в БД
     */
    List<AuditEntry> findAll();
}
