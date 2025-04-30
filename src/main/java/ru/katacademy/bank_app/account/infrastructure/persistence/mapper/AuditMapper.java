package ru.katacademy.bank_app.account.infrastructure.persistence.mapper;

import ru.katacademy.bank_app.account.domain.entity.AuditEntry;
import ru.katacademy.bank_app.account.infrastructure.persistence.entity.AuditEntryEntity;

public class AuditMapper {
    public static AuditEntryEntity toEntity(AuditEntry auditEntry) {
        return new AuditEntryEntity(
                auditEntry.getUserId(),
                auditEntry.getAction(),
                auditEntry.getTimestamp(),
                auditEntry.getDetails(),
                String.valueOf(auditEntry.getUserId())
        );
    }

    public static AuditEntry toDomain(AuditEntryEntity entity) {
        return new AuditEntry(
                Long.parseLong(entity.getActor()),
                entity.getEventType(),
                entity.getPayload(),
                entity.getTimestamp(),
                "Статус операции"
        );
    }
}
