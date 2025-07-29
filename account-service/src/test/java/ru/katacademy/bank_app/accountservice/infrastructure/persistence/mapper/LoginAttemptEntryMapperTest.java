package ru.katacademy.bank_app.accountservice.infrastructure.persistence.mapper;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import ru.katacademy.bank_app.accountservice.domain.entity.LoginAttemptEntry;
import ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity.LoginAttemptEntryEntity;

class LoginAttemptEntryMapperTest {

    @Test
    void roundTrip() {
        final LocalDateTime now = LocalDateTime.now();
        final LoginAttemptEntry original = new LoginAttemptEntry(2L,
                "attempt-123",
                "92.168.0.0",
                "user-456",
                now,
                false
        );

        final LoginAttemptEntryEntity entity = LoginAttemptEntryMapper.toEntity(original);
        assertEquals(original.getId(),        entity.getId());
        assertEquals(original.getUserId(),    entity.getUserId());
        assertEquals(original.getEmail(), entity.getEmail());

        final LoginAttemptEntry back = LoginAttemptEntryMapper.toDomain(entity);
        assertEquals(original.getId(),       back.getId());
        assertEquals(original.getUserId(),   back.getUserId());
        assertEquals(original.getEmail(),    back.getEmail());
    }
}
