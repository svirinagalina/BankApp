package ru.katacademy.notification.infrastructure.mapper;

import org.junit.jupiter.api.Test;
import ru.katacademy.notification.domain.model.NotificationLogEntry;
import ru.katacademy.notification.infrastructure.persistence.entity.NotificationLog;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificationLogMapperTest {

    private final NotificationLogMapper mapper = new NotificationLogMapper();

    @Test
    void roundTrip() {
        LocalDateTime ts = LocalDateTime.parse("2025-07-24T12:34:56");
        NotificationLogEntry original = new NotificationLogEntry(null, "Hello", ts);

        NotificationLog entity = mapper.toEntity(original);
        assertNull(entity.getId());
        assertEquals("Hello", entity.getMessage());
        assertEquals(ts, entity.getTimestamp());

        entity.setId(42L);
        NotificationLogEntry back = mapper.toDomain(entity);
        assertEquals(42L, back.getId());
        assertEquals("Hello", back.getMessage());
        assertEquals(ts, back.getTimestamp());
    }
}
