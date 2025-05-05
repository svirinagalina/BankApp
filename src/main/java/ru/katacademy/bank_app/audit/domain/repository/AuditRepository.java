package ru.katacademy.bank_app.audit.domain.repository;

import ru.katacademy.bank_app.audit.domain.entity.AuditEntry;

import java.util.List;

public interface AuditRepository {
    void save(AuditEntry auditEntry);

    List<AuditEntry> getAllAudits();

    List<AuditEntry> getAllAuditsByType(String eventType);
}

