package ru.katacademy.bank_app.account.domain.repository;

import ru.katacademy.bank_app.account.domain.entity.AuditEntry;

public interface AuditRepository {
    void save(AuditEntry auditEntry);
}
