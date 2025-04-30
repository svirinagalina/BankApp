package ru.katacademy.bank_app.audit.domain.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Класс представляет модель для бизнес логики аудита.
 * <p>
 * Содержит информацию о действиях пользователей в системе, включая метаданные
 * о времени выполнения и статусе операции.
 */
@Data
@NoArgsConstructor
@Getter
@Setter
public class AuditEntry {

    private Long userId;

    private String action;

    private String details;

    private LocalDateTime timestamp;

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
        return userId.equals(that.userId) && timestamp.equals(that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, timestamp);
    }
}
