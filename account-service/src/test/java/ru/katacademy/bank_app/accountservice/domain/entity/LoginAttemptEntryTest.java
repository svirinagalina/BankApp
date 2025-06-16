package ru.katacademy.bank_app.accountservice.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для класса LoginAttemptEntry, проверяющие:
 * - Корректность создания объекта через конструкторы
 * - Работу сеттеров и геттеров для полей
 */

class LoginAttemptEntryTest {

    @Test
    @DisplayName("Тест 1: Проверка конструктора без параметров")
    void testNoArgsConstructor() {
        // Вызов тестируемого конструктора
        final LoginAttemptEntry entry = new LoginAttemptEntry();

        // Проверки всех полей объекта
        assertNotNull(entry);
        assertNull(entry.getId());
        assertNull(entry.getUserId());
        assertNull(entry.getEmail());
        assertNull(entry.getIp());
        assertNull(entry.getUserAgent());
        assertNull(entry.getTimestamp());
        assertFalse(entry.isSuccess());
    }

    @Test
    @DisplayName("Тест 2: Проверка конструктора со всеми параметрами ")
    void testAllArgsConstructor() {
        // Подготовка тестовых данных
        final Long userId = 1L;
        final String email = "test@mail.com";
        final String ip = "192.168.0.1";
        final String userAgent = "Mozilla/4.0";
        final LocalDateTime timestamp = LocalDateTime.now();
        final boolean success = true;

        // Вызов тестируемого конструктора
        final LoginAttemptEntry entry = new LoginAttemptEntry(userId, email, ip, userAgent, timestamp, success);

        // Проверки всех полей объекта
        assertNotNull(entry);
        assertNull(entry.getId()); // ID генерируется базой данных
        assertEquals(userId, entry.getUserId());
        assertEquals(email, entry.getEmail());
        assertEquals(ip, entry.getIp());
        assertEquals(userAgent, entry.getUserAgent());
        assertEquals(timestamp, entry.getTimestamp());
        assertEquals(success, entry.isSuccess());
    }

    @Test
    @DisplayName("Тест 3:Тест сеттеров и геттеров - должен корректно устанавливать и возвращать значения")
    void testSettersAndGetters() {
        // Подготовка тестовых данных
        final LoginAttemptEntry entry = new LoginAttemptEntry();
        final Long id = 10L;
        final Long userId = 2L;
        final String email = "test@mail.com";
        final String ip = "192.168.0.1";
        final String userAgent = "Mozilla/4.0";
        final LocalDateTime timestamp = LocalDateTime.of(2025, 6, 16, 12, 0);
        final boolean success = false;

        // Вызов Сеттеров
        entry.setId(id);
        entry.setUserId(userId);
        entry.setEmail(email);
        entry.setIp(ip);
        entry.setUserAgent(userAgent);
        entry.setTimestamp(timestamp);
        entry.setSuccess(success);

        // Проверки полей объекта
        assertEquals(id, entry.getId());
        assertEquals(userId, entry.getUserId());
        assertEquals(email, entry.getEmail());
        assertEquals(ip, entry.getIp());
        assertEquals(userAgent, entry.getUserAgent());
        assertEquals(timestamp, entry.getTimestamp());
        assertEquals(success, entry.isSuccess());
    }


}