package ru.katacademy.bank_app.audit.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для класса AuditEntry, проверяющие:
 * - Корректность создания объекта через конструктор
 * - Корректность создания объекта через конструктор с null userId
 * - Проверка equals и hashCode
 * - Проверка метода toString
 */

class AuditEntryTest {

    @Test
    @DisplayName("Тест 1: Проверка основного конструктора на корректность всех полей")
    void testConstructorAndGetters() {
        // Подготовка тестовых данных
        final String expectedEventType = "USER_LOGIN";
        final String expectedMessage = "User logged in";
        final String expectedUserId = "user1";

        // Вызов тестируемого конструктора
        final AuditEntry auditEntry = new AuditEntry(expectedEventType, expectedMessage, expectedUserId);

        // Проверки всех полей объекта
        assertEquals(expectedEventType, auditEntry.getEventType(),
                "Тип события должен соответствовать переданному в конструктор");
        assertEquals(expectedMessage, auditEntry.getMessage(),
                "Сообщение должно соответствовать переданному в конструктор");
        assertEquals(expectedUserId, auditEntry.getUserId(),
                "ID пользователя должно соответствовать переданному в конструктор");

        // Проверяем timestamp
        assertNotNull(auditEntry.getTimestamp(), "Timestamp не должен быть null");
    }

    @Test
    @DisplayName("Тест 2: Проверка конструктора с null userId ")
    void testConstructorWithNullUserId() {
        // Подготовка тестовых данных
        final String eventType = "SYSTEM_EVENT";
        final String message = "System maintenance started";

        // Вызов тестируемого конструктора
        final AuditEntry auditEntry = new AuditEntry(eventType, message, null);

        // Проверки полей объекта
        assertEquals(eventType, auditEntry.getEventType(),
                "Тип события должен быть установлен корректно");
        assertEquals(message, auditEntry.getMessage(),
                "Сообщение должно быть установлено корректно");
        assertNull(auditEntry.getUserId(),
                "userId должен быть null при передаче null в конструктор");
        assertNotNull(auditEntry.getTimestamp(),
                "Timestamp должен быть установлен даже при null userId");
    }

    @Test
    @DisplayName("Тест 3: Проверка equals и hashCode")
    void testEqualsAndHashCode() {
        // Подготовка тестовых данных
        final Instant fixedTimestamp = Instant.now();
        final AuditEntry entry1 = new AuditEntry("TRANSACTION", "Money transfer", "user1");
        final AuditEntry entry2 = new AuditEntry("TRANSACTION", "Money transfer", "user1");

        // Для тестирования equals устанавливаем одинаковый timestamp
        try {
            final Field timestampField = AuditEntry.class.getDeclaredField("timestamp");
            timestampField.setAccessible(true);
            timestampField.set(entry1, fixedTimestamp);
            timestampField.set(entry2, fixedTimestamp);
        } catch (Exception e) {
            fail("Не удалось установить timestamp для теста через рефлексию");
        }

        // Проверка
        assertEquals(entry1, entry2,
                "Объекты с одинаковыми полями должны быть равны");
        assertEquals(entry1.hashCode(), entry2.hashCode(),
                "hashCode должен быть одинаковым для равных объектов");
    }


    @Test
    @DisplayName("Тест 4: Проверка метода toString ")
    void testToString() {
        // Подготовка тестовых данных
        final String eventType = "LOGOUT";
        final String message = "User logged out";
        final String userId = "user1";
        final AuditEntry auditEntry = new AuditEntry(eventType, message, userId);

        // Выполнение
        final String resultString = auditEntry.toString();

        // Проверка всех полей в строке
        assertTrue(resultString.contains("AuditEntry"),
                "Строка должна содержать имя класса");
        assertTrue(resultString.contains("eventType='" + eventType + "'"),
                "Строка должна содержать тип события");
        assertTrue(resultString.contains("message='" + message + "'"),
                "Строка должна содержать сообщение");
        assertTrue(resultString.contains("userId='" + userId + "'"),
                "Строка должна содержать ID пользователя");
        assertTrue(resultString.contains("timestamp="),
                "Строка должна содержать timestamp");
    }
}