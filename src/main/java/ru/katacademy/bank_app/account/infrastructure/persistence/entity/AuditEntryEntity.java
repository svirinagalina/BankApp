package ru.katacademy.bank_app.account.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Сущность для хранения аудита событий.
 * Представляет собой запись о событии, которое произошло в системе.
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class AuditEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Тип события: TRANSFER или REGISTRATION
     */
    @Column(nullable = false)
    private String eventType;

    /**
     * Время события
     */
    @Column(nullable = false)
    private LocalDateTime timestamp;

    /**
     * JSON-представление события
     */
    @Lob
    @Column(nullable = false)
    private String payload;

    /**
     * Кто инициировал событие (например, email или userId)
     */
    @Column(nullable = false)
    private String actor;

}
