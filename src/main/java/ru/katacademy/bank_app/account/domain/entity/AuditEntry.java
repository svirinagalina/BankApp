package ru.katacademy.bank_app.account.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

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
    private Long userId;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String details;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String status;

    public AuditEntry(Long userId, String action, String details, LocalDateTime timestamp, String status) {
        this.userId = userId;
        this.action = action;
        this.details = details;
        this.timestamp = timestamp;
        this.status = status;
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
        return id.equals(that.id) && userId.equals(that.userId) && timestamp.equals(that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, timestamp);
    }
}
