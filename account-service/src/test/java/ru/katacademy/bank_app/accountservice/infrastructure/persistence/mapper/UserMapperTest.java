package ru.katacademy.bank_app.accountservice.infrastructure.persistence.mapper;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import ru.katacademy.bank_app.accountservice.domain.entity.User;
import ru.katacademy.bank_app.accountservice.domain.enumtype.UserRole;
import ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity.UserEntity;
import ru.katacademy.bank_shared.valueobject.Email;

class UserMapperTest {

    @Test
    void roundTrip() {
        final LocalDateTime now = LocalDateTime.now();
        final User original = new User(
                1L,
                UserRole.USER,
                "Ivan Petrov",
                new Email("ivan@example.com"),
                "hashPassword",
                now
        );

        final UserEntity entity = UserMapper.toEntity(original);
        final User back = UserMapper.toDomain(entity);

        assertEquals(original.getId(),        back.getId());
        assertEquals(original.getRole(),      back.getRole());
        assertEquals(original.getFullName(),  back.getFullName());
        assertEquals(original.getEmail(),     back.getEmail());
        assertEquals(original.getPasswordHash(),  back.getPasswordHash());
        assertEquals(original.getCreatedAt(), back.getCreatedAt());
        assertEquals(original, back);
    }
}
