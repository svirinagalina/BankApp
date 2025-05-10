package ru.katacademy.bank_app.audit.domain.entity;

import java.time.Instant;
import java.util.Objects;

/**
 * Представляет собой запись аудита, фиксирующую событие, произошедшее в системе.
 * Каждая запись содержит тип события, сообщение, идентификатор пользователя (если применимо)
 * и временную метку, указывающую момент создания события.
 * <p>
 * Поля:
 * - eventType: тип события
 * - message: текстовое описание события
 * - userId: идентификатор пользователя, вызвавшего событие, либо {@code null}, если событие системное
 * - timestamp: временная метка
 * <p>
 * Автор: Maxim4212
 * Дата: 2025-05-10
 */
public class AuditEntry {
    private final String eventType;
    private final String message;
    private final String userId;
    private final Instant timestamp;

    public AuditEntry(String eventType, String message, String userId) {
        this.eventType = eventType;
        this.message = message;
        this.userId = userId;
        this.timestamp = Instant.now();
    }

    public String getEventType() {
        return eventType;
    }

    public String getMessage() {
        return message;
    }

    public String getUserId() {
        return userId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "AuditEntry{" +
                ", eventType='" + eventType + '\'' +
                ", message='" + message + '\'' +
                ", userId='" + userId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final AuditEntry that = (AuditEntry) o;

        return Objects.equals(eventType, that.eventType)
                && Objects.equals(message, that.message)
                && Objects.equals(userId, that.userId)
                && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventType, message, userId, timestamp);
    }
}
