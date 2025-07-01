package ru.katacademy.bank_app.accountservice.domain.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.katacademy.bank_app.accountservice.domain.enumtype.UserRole;
import ru.katacademy.bank_shared.valueobject.Email;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для {@link User} - сущности пользователя системы.
 * Проверяет корректность работы методов класса User, включая проверку ролей,
 * доступ к полям и модификацию данных пользователя.
 */
class UserTest {

    private User user;
    private User adminUser;
    private LocalDateTime testTime;

    /**
     * Инициализация тестовых данных перед каждым тестом.
     * Создает:
     * 1. Обычного пользователя (USER)
     * 2. Администратора (ADMIN)
     * 3. Тестовую метку времени
     */
    @BeforeEach
    void setUp() {
        testTime = LocalDateTime.now();
        final Email email = new Email("john.doe@example.com");
        final Email adminEmail = new Email("admin@example.com");

        user = new User(
                1L,
                UserRole.USER,
                "John Doe",
                email,
                "hashed_password_here",
                testTime
        );

        adminUser = new User(
                2L,
                UserRole.ADMIN,
                "Admin",
                adminEmail,
                "admin_password_hash",
                testTime
        );
    }

    /**
     * Тест проверки роли администратора.
     * Проверяет что:
     * 1. Обычный пользователь не является администратором
     * 2. Пользователь с ролью ADMIN определяется как администратор
     */
    @Test
    void isAdmin() {
        assertFalse(user.isAdmin());
        assertTrue(adminUser.isAdmin());
    }

    /**
     * Тест проверки роли обычного пользователя.
     * Проверяет что:
     * 1. Обычный пользователь определяется корректно
     * 2. Администратор не определяется как обычный пользователь
     */
    @Test
    void isUser() {
        assertTrue(user.isUser());
        assertFalse(adminUser.isUser());
    }

    /**
     * Тест получения идентификатора пользователя.
     * Проверяет корректность возвращаемого значения ID.
     */
    @Test
    void getId() {
        assertEquals(1L, user.getId());
        assertEquals(2L, adminUser.getId());
    }

    /**
     * Тест получения роли пользователя.
     * Проверяет корректность возвращаемого значения роли.
     */
    @Test
    void getRole() {
        assertEquals(UserRole.USER, user.getRole());
        assertEquals(UserRole.ADMIN, adminUser.getRole());
    }

    /**
     * Тест получения полного имени пользователя.
     * Проверяет корректность возвращаемого значения имени.
     */
    @Test
    void getFullName() {
        assertEquals("John Doe", user.getFullName());
        assertEquals("Admin", adminUser.getFullName());
    }

    /**
     * Тест получения email пользователя.
     * Проверяет корректность возвращаемого объекта Email.
     */
    @Test
    void getEmail() {
        assertEquals(new Email("john.doe@example.com"), user.getEmail());
        assertEquals(new Email("admin@example.com"), adminUser.getEmail());
    }

    /**
     * Тест получения хэша пароля.
     * Проверяет корректность возвращаемого значения хэша пароля.
     */
    @Test
    void getPasswordHash() {
        assertEquals("hashed_password_here", user.getPasswordHash());
        assertEquals("admin_password_hash", adminUser.getPasswordHash());
    }

    /**
     * Тест получения даты создания пользователя.
     * Проверяет корректность возвращаемой метки времени.
     */
    @Test
    void getCreatedAt() {
        assertEquals(testTime, user.getCreatedAt());
        assertEquals(testTime, adminUser.getCreatedAt());
    }

    /**
     * Тест установки нового хэша пароля.
     * Проверяет что:
     * 1. Метод setPasswordHash корректно изменяет значение
     * 2. Новое значение правильно сохраняется
     */
    @Test
    void setPasswordHash() {
        user.setPasswordHash("new_hashed_password");
        assertEquals("new_hashed_password", user.getPasswordHash());
    }
}