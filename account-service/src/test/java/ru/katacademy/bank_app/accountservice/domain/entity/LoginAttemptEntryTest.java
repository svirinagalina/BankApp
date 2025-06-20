package ru.katacademy.bank_app.accountservice.domain.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для {@link LoginAttemptEntry} - сущности записи попытки входа.
 * Проверяет корректность работы всех методов доступа и модификации полей.
 */
class LoginAttemptEntryTest {

    private LoginAttemptEntry entry;
    private LocalDateTime testTime;

    /**
     * Инициализация тестовых данных перед каждым тестом.
     * Создает тестовую запись о попытке входа с параметрами.
     */
    @BeforeEach
    void setUp() {
        testTime = LocalDateTime.now();
        entry = new LoginAttemptEntry(
                1L,
                "user@mail.com",
                "192.168.1.1",
                "Mozilla/5.0",
                testTime,
                true
        );
    }

    /**
     * Тест получения ID записи.
     * Проверяет что изначально ID равен null (генерируется БД).
     */
    @Test
    void getId() {
        assertNull(entry.getId());
    }

    /**
     * Тест получения ID пользователя.
     * Проверяет корректность возвращаемого значения.
     */
    @Test
    void getUserId() {
        assertEquals(1L, entry.getUserId());
    }

    /**
     * Тест получения email пользователя.
     * Проверяет корректность возвращаемого значения.
     */
    @Test
    void getEmail() {
        assertEquals("user@mail.com", entry.getEmail());
    }

    /**
     * Тест получения IP-адреса.
     * Проверяет корректность возвращаемого значения.
     */
    @Test
    void getIp() {
        assertEquals("192.168.1.1", entry.getIp());
    }

    /**
     * Тест получения User-Agent.
     * Проверяет корректность возвращаемого значения.
     */
    @Test
    void getUserAgent() {
        assertEquals("Mozilla/5.0", entry.getUserAgent());
    }

    /**
     * Тест получения временной метки.
     * Проверяет корректность возвращаемого значения времени.
     */
    @Test
    void getTimestamp() {
        assertEquals(testTime, entry.getTimestamp());
    }

    /**
     * Тест проверки успешности попытки входа.
     * Проверяет корректность возвращаемого статуса.
     */
    @Test
    void isSuccess() {
        assertTrue(entry.isSuccess());
    }

    /**
     * Тест установки ID записи.
     * Проверяет корректность изменения значения ID.
     */
    @Test
    void setId() {
        entry.setId(100L);
        assertEquals(100L, entry.getId());
    }

    /**
     * Тест установки ID пользователя.
     * Проверяет корректность изменения значения.
     */
    @Test
    void setUserId() {
        entry.setUserId(2L);
        assertEquals(2L, entry.getUserId());
    }

    /**
     * Тест установки email пользователя.
     * Проверяет корректность изменения значения.
     */
    @Test
    void setEmail() {
        entry.setEmail("new@mail.com");
        assertEquals("new@mail.com", entry.getEmail());
    }

    /**
     * Тест установки IP-адреса.
     * Проверяет корректность изменения значения.
     */
    @Test
    void setIp() {
        entry.setIp("10.0.0.1");
        assertEquals("10.0.0.1", entry.getIp());
    }

    /**
     * Тест установки User-Agent.
     * Проверяет корректность изменения значения.
     */
    @Test
    void setUserAgent() {
        entry.setUserAgent("Chrome/120.0");
        assertEquals("Chrome/120.0", entry.getUserAgent());
    }

    /**
     * Тест установки временной метки.
     * Проверяет корректность изменения значения времени.
     */
    @Test
    void setTimestamp() {
        final LocalDateTime newTime = LocalDateTime.now().plusDays(1);
        entry.setTimestamp(newTime);
        assertEquals(newTime, entry.getTimestamp());
    }

    /**
     * Тест установки статуса попытки входа.
     * Проверяет корректность изменения значения статуса.
     */
    @Test
    void setSuccess() {
        entry.setSuccess(false);
        assertFalse(entry.isSuccess());
    }
}