package ru.katacademy.bank_app.accountservice.domain.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.katacademy.bank_app.accountservice.application.dto.UserDto;
import ru.katacademy.bank_app.accountservice.domain.entity.User;
import ru.katacademy.bank_app.accountservice.domain.enumtype.UserRole;
import ru.katacademy.bank_shared.valueobject.Email;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Тесты для класса UserMapper, проверяющие:
 * - Проверка преобразования User в UserDto
 */

class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    @Test
    @DisplayName("Тест 1: Проверка преобразования User в UserDto")
    void toDto_ShouldCorrectlyMapUserToDto() {
        // Создаем тестовый объект Email
        final Email email = new Email("test@mail.com");

        // Создаем тестового пользователя через конструктор
        final User user = new User(
                1L,
                UserRole.USER,
                "Андрей Кузин",
                email,
                "hashed_password",
                LocalDateTime.now()
        );

        // Проверки всех полей объекта
        final  UserDto dto = mapper.toDto(user);

        // Проверки всех полей объекта
        assertNotNull(dto, "DTO не должно быть null");
        assertEquals(1L, dto.id(), "ID должно соответствовать");
        assertEquals("Андрей Кузин", dto.fullName(), "FullName должно соответствовать");
        assertEquals("test@mail.com", dto.email(), "Email должно соответствовать");
    }
}