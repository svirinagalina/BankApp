package ru.katacademy.bank_app.accountservice.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity.LoginAttemptEntryEntity;
import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA‑репозиторий для {@link LoginAttemptEntryEntity}.
 * CRUD + поиск по userId, по диапазону timestamp и по success.
 */
public interface LoginAttemptEntryJpaRepository
        extends JpaRepository<LoginAttemptEntryEntity, Long> {

    /**
     * Находит все записи по {@code userId}.
     */
    List<LoginAttemptEntryEntity> findByUserId(Long userId);

    /**
     * Находит по диапазону timestamp (включительно).
     */
    List<LoginAttemptEntryEntity> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Находит по флагу успешности.
     */
    List<LoginAttemptEntryEntity> findBySuccess(boolean success);
}
