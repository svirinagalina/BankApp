package ru.katacademy.bank_app.accountservice.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.katacademy.bank_app.accountservice.domain.entity.User;
import ru.katacademy.bank_app.accountservice.domain.enumtype.UserRole;
import ru.katacademy.bank_shared.valueobject.Email;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для класса User, проверяющие:
 * - Корректность создания объекта через конструкторы
 * - Бизнес-логику (проверка ролей)
 * - Работу сеттеров для изменяемого поля passwordHash
 */

@DisplayName("Тесты для класса User")
class UserTest {
    // Общие тестовые данные для всех тестов
    private static final Email VALID_EMAIL = new Email("test@mail.com");
    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2025, 6, 15, 12, 0);
    private static final String PASSWORD_HASH = "$9a$10$N9qo34LOickgx2ZAndrey";

    @Test
    @DisplayName("Тест 1: Проверка основного конструктора на корректность всех полей")
    void constructor_ShouldSetAllFields_WhenAllArgsProvided() {
        // Подготовка тестовых данных
        final Long id = 1L;
        final UserRole role = UserRole.ADMIN;
        final String name = "Johnny Depp";

        // Вызов тестируемого конструктора
        final User user = new User(
                id,
                role,
                name,
                VALID_EMAIL,
                PASSWORD_HASH,
                FIXED_TIME
        );

        // Проверки всех полей объекта
        assertEquals(id, user.getId(),
                "ID пользователя должно соответствовать переданному значению");
        assertEquals(role, user.getRole(),
                "Роль пользователя должна соответствовать переданному значению");
        assertEquals(name, user.getFullName(),
                "Полное имя должно соответствовать переданному значению");
        assertEquals(VALID_EMAIL, user.getEmail(),
                "Email должен соответствовать переданному значению");
        assertEquals(PASSWORD_HASH, user.getPasswordHash(),
                "Хеш пароля должен соответствовать переданному значению");
        assertEquals(FIXED_TIME, user.getCreatedAt(),
                "Дата создания должна соответствовать переданному значению");
    }

    @Test
    @DisplayName("Тест 2: Конструктор без ID должен создавать объект с null ID")
    void constructor_ShouldAllowNullId_ForNewEntities() {
        // Создание пользователя через конструктор без ID
        final User user = new User(
                UserRole.USER,
                "Andrey",
                VALID_EMAIL,
                PASSWORD_HASH,
                FIXED_TIME
        );

        // Проверка, что ID остался null
        assertNull(user.getId(),
                "ID должен быть null при создании через конструктор без ID");
    }

    @Test
    @DisplayName("Тест 3: Конструктор должен бросать исключение при null-роли")
    void constructor_ShouldThrowException_WhenRoleIsNull() {
        // Проверка исключения при null-роли
        assertThrows(
                NullPointerException.class,
                () -> new User(null, null, "Bob", VALID_EMAIL, PASSWORD_HASH, FIXED_TIME),
                "Конструктор должен бросать исключение при null-роли"
        );
    }

    @Test
    @DisplayName("Тест 4: Метод isAdmin() должен возвращать true для роли ADMIN")
    void isAdmin_ShouldReturnTrue_ForAdminRole() {
        // Создание пользователя с ролью ADMIN
        final User admin = new User(
                UserRole.ADMIN,
                "Andrey",
                VALID_EMAIL,
                PASSWORD_HASH,
                FIXED_TIME
        );

        // Проверка метода isAdmin()
        assertTrue(admin.isAdmin(),
                "Метод isAdmin() должен возвращать true для роли ADMIN");
    }

    @Test
    @DisplayName("Тест 5: Метод isAdmin() должен возвращать false для других ролей")
    void isAdmin_ShouldReturnFalse_ForNonAdminRole() {
        // Создание обычного пользователя
        final User user = new User(
                UserRole.USER,
                "User",
                VALID_EMAIL,
                PASSWORD_HASH,
                FIXED_TIME
        );

        // Проверка метода isAdmin()
        assertFalse(user.isAdmin(),
                "Метод isAdmin() должен возвращать false для ролей, отличных от ADMIN");
    }

    @Test
    @DisplayName("Тест 6: Сеттер passwordHash должен корректно обновлять значение")
    void setPasswordHash_ShouldUpdateField() {
        // Создание пользователя
        final User user = new User(
                UserRole.USER,
                "User",
                VALID_EMAIL,
                "$9a$10$N9qo34LOickgx2ZAndrey",
                FIXED_TIME
        );

        // Установка нового значения
        final String newHash = "$9a$10$N9qo34LOickgx2ZNewHash";
        user.setPasswordHash(newHash);

        // Проверка обновления значения
        assertEquals(newHash, user.getPasswordHash(),
                "Сеттер passwordHash должен обновлять значение поля");
    }

}