package ru.katacademy.bank_app.audit.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * Класс-сущность, нужен для записи аудита в базу данных.
 * <p>
 * Поля:
 * - id: Уникальный идентификатор записи аудита.
 * - timestamp: Метка времени, когда произошло событие аудита.
 * - eventType: Тип события
 * - message: Сообщение, предоставляющее дополнительную информацию о событии аудита.
 * - userId: Идентификатор пользователя, связанного с событием аудита.
 * <p>
 * Автор: Maxim4212
 * Дата: 2025-05-10
 */
@Entity
@Table(name = "audit_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "timestamp")
    private String timestamp;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "message")
    private String message;

    @Column(name = "user_id")
    private String userId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AuditEntryEntity that = (AuditEntryEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(timestamp, that.timestamp)
                && Objects.equals(eventType, that.eventType) && Objects.equals(message, that.message)
                && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timestamp, eventType, message, userId);
    }
}
