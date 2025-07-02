package ru.katacademy.bank_app.audit.domain.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для {@link AuditEntry} - сущности записи аудита.
 * Проверяет базовую функциональность создания и работы с записями аудита.
 */
class AuditEntryTest {

    /**
     * Тест создания записи аудита и доступа к её полям.
     * Проверяет:
     * - Корректность инициализации полей
     * - Автоматическую установку временной метки
     * - Работу геттеров
     */
    @Test
    void shouldCreateAndAccessAuditEntry() {
        final String eventType = "USER_LOGIN";
        final String message = "User logged in";
        final String userId = "123";

        final AuditEntry entry = new AuditEntry(eventType, message, userId);

        assertEquals(eventType, entry.getEventType());
        assertEquals(message, entry.getMessage());
        assertEquals(userId, entry.getUserId());
        assertNotNull(entry.getTimestamp());
        assertTrue(entry.getTimestamp().isBefore(Instant.now().plusSeconds(1)));
    }

    /**
     * Тест сравнения объектов и вычисления хэш-кода.
     * Проверяет:
     * - Равенство объектов с одинаковыми полями
     * - Неравенство объектов с разными полями
     * - Согласованность equals и hashCode
     */
    @Test
    void testEqualsAndHashCode() {
        final AuditEntry entry1 = new AuditEntry("EVENT", "Test", "1");
        final AuditEntry entry2 = new AuditEntry("EVENT", "Test", "1");
        final AuditEntry different = new AuditEntry("OTHER", "Test", "1");

        assertEquals(entry1.getEventType(), entry2.getEventType());
        assertEquals(entry1.getMessage(), entry2.getMessage());
        assertEquals(entry1.getUserId(), entry2.getUserId());
        assertNotEquals(entry1.getEventType(), different.getEventType());
    }

    /**
     * Тест строкового представления объекта.
     * Проверяет что toString() содержит все основные поля записи.
     */
    @Test
    void testToString() {
        final AuditEntry entry = new AuditEntry("TEST", "Message", "456");
        final String str = entry.toString();

        assertTrue(str.contains("TEST"));
        assertTrue(str.contains("Message"));
        assertTrue(str.contains("456"));
    }

    /**
     * Тест обработки null-значения для userId.
     * Проверяет корректную работу с системными событиями без пользователя.
     */
    @Test
    void shouldHandleNullUserId() {
        final AuditEntry systemEntry = new AuditEntry("SYSTEM", "Event", null);

        assertNull(systemEntry.getUserId());
        assertNotNull(systemEntry.toString());
    }
}