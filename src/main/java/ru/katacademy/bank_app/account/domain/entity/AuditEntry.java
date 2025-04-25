package ru.katacademy.bank_app.account.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

/**
 * Класс представляет запись в журнале аудита.
 * <p>
 * Содержит информацию о действиях пользователей в системе, включая метаданные
 * о времени выполнения и статусе операции.
 * Является JPA-сущностью для сохранения в БД.
 */
@Entity
@Data
@NoArgsConstructor
@Getter
@Setter
@Table
public class AuditEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NonNull
    private Long userId;

    @Column(nullable = false)
    @NonNull
    private String action;

    @Column(nullable = false)
    @NonNull
    private String details;

    @Column(nullable = false)
    @NonNull
    private Timestamp timestamp;

    @Column(nullable = false)
    @NonNull
    private String status;

    public AuditEntry(Long userId, String action, String details,
                      Timestamp timestamp, String status) {
        this.userId = userId;
        this.action = action;
        this.details = details;
        this.timestamp = timestamp;
        this.status = status;
    }
}
