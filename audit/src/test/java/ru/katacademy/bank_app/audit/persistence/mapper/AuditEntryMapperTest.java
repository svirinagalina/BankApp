package ru.katacademy.bank_app.audit.persistence.mapper;

import org.junit.jupiter.api.Test;
import ru.katacademy.bank_app.audit.domain.entity.AuditEntry;
import ru.katacademy.bank_app.audit.persistence.entity.AuditEntryEntity;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class AuditEntryMapperTest {

    @Test
    void roundTrip() {
        final Instant fixed = Instant.parse("2025-07-24T17:00:10.896512600Z");

        final AuditEntry original = new AuditEntry("TYPE", "msg", "user", fixed);

        final AuditEntryEntity entity = AuditEntryMapper.toEntity(original);
        assertEquals("TYPE", entity.getEventType());
        assertEquals("msg",  entity.getMessage());
        assertEquals("user", entity.getUserId());
        assertEquals(fixed.toString(), entity.getTimestamp());

        final AuditEntry back = AuditEntryMapper.toDomain(entity);
        assertEquals(original, back);
    }
}
