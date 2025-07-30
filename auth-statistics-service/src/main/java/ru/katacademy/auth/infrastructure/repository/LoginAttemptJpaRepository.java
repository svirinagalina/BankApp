package ru.katacademy.auth.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.katacademy.auth.infrastructure.persistence.entity.LoginAttemptEntity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data JPA‑репозиторий для {@link LoginAttemptEntity}.
 * Предоставляет методы поиска по дополнительным критериям.
 */
public interface LoginAttemptJpaRepository
        extends JpaRepository<LoginAttemptEntity, Long> {

    /**
     * Находит все JPA‑сущности попыток входа по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список JPA‑сущностей
     */
    List<LoginAttemptEntity> findByUserId(Long userId);

    /**
     * Находит все JPA‑сущности попыток входа за указанный период.
     *
     * @param start начало диапазона
     * @param end   конец диапазона
     * @return список JPA‑сущностей
     */
    List<LoginAttemptEntity> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Находит все JPA‑сущности попыток входа по успешности.
     *
     * @param success статус успешности
     * @return список JPA‑сущностей
     */
    List<LoginAttemptEntity> findBySuccess(boolean success);

    List<LoginAttemptEntity> findByUserIdAndTimestampBetween(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<LoginAttemptEntity> findByUserIdAndSuccess(
            Long userId,
            boolean success
    );

    List<LoginAttemptEntity> findByUserIdAndTimestampBetweenAndSuccess(
            Long userId,
            LocalDateTime start,
            LocalDateTime end,
            boolean success
    );
}
