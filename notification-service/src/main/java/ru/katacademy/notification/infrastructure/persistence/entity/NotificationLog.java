package ru.katacademy.notification.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Сущность для хранения истории отправленных уведомлений.
 */
@Entity
@Getter
@Setter
@ToString(exclude = {"message"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "notification_log")
public class NotificationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String message;

    private LocalDateTime timestamp;

    public NotificationLog() {

    }

    public NotificationLog(String message, LocalDateTime timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }
}
