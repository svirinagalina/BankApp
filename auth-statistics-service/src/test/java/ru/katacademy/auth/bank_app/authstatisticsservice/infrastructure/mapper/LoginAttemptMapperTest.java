package ru.katacademy.auth.bank_app.authstatisticsservice.infrastructure.mapper;

import org.junit.jupiter.api.Test;
import ru.katacademy.auth.domain.entity.LoginAttempt;
import ru.katacademy.auth.infrastructure.persistence.entity.LoginAttemptEntity;
import ru.katacademy.auth.infrastructure.mapper.LoginAttemptMapper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LoginAttemptMapperTest {

    @Test
    void roundTrip() {
        LocalDateTime fixed = LocalDateTime.parse("2025-07-24T10:15:30");

        LoginAttempt original = new LoginAttempt(
                1L,                     // id
                42L,                    // userId
                fixed,                  // timestamp
                true,                   // success
                "127.0.0.1",            // ip
                "JUnit-Agent"           // userAgent
        );

        LoginAttemptEntity entity = new LoginAttemptMapper().toEntity(original);
        assertEquals(original.getId(),        entity.getId());
        assertEquals(original.getUserId(),    entity.getUserId());
        assertEquals(original.getTimestamp(), entity.getTimestamp());
        assertEquals(original.isSuccess(),    entity.isSuccess());
        assertEquals(original.getIp(),        entity.getIp());
        assertEquals(original.getUserAgent(), entity.getUserAgent());

        LoginAttempt back = new LoginAttemptMapper().toDomain(entity);
        assertEquals(original.getId(),        back.getId());
        assertEquals(original.getUserId(),    back.getUserId());
        assertEquals(original.getTimestamp(), back.getTimestamp());
        assertEquals(original.isSuccess(),    back.isSuccess());
        assertEquals(original.getIp(),        back.getIp());
        assertEquals(original.getUserAgent(), back.getUserAgent());
    }
}
