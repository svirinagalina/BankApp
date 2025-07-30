package ru.katacademy.auth.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.katacademy.auth.infrastructure.persistence.entity.LoginAttemptEntity;
import ru.katacademy.auth.infrastructure.repository.LoginAttemptAuthRepositoryImpl;


import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA-репозиторий для работы с попытками входа в систему.
 * <p>
 * Предоставляет стандартные CRUD-операции через {@link JpaRepository}
 * и кастомные запросы для сущности {@link LoginAttemptEntity}.
 * Все методы работают с JPA-сущностями, преобразование в domain-объекты
 * выполняется в {@link LoginAttemptAuthRepositoryImpl}.
 * Работает напрямую с таблицей {@code auth_login_attempts}
 * </p>
 *
 * @author MihasBatler
 * @see LoginAttemptEntity Сущность, с которой работает репозиторий
 */
public interface JpaLoginAttemptAuthRepository extends JpaRepository<LoginAttemptEntity, Long> {

    /**
     * Находит все попытки входа по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя (не может быть {@code null})
     * @return список попыток входа или пустой список, если ничего не найдено
     */
    List<LoginAttemptEntity> findByUserId(Long userId);

    /**
     * Находит все попытки входа в указанном временном диапазоне.
<<<<<<< HEAD:auth-statistics-service/src/main/java/ru/katacademy/auth/domain/repository/JpaLoginAttemptAuthRepository.java
=======
     * @param start начальная дата диапазона
     * @param end   конечная дата диапазона
     * @return список попыток входа или пустой список, если ничего не найдено
     */
    List<LoginAttemptEntity> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Находит все попытки входа по указанному результату.
     *
     * @param success результат попытки:
     *                {@code true} - успешные,
     *                {@code false} - неудачные
     * @return список попыток входа или пустой список, если ничего не найдено
     */
    List<LoginAttemptEntity> findBySuccess(boolean success);

    /**
     * Находит все попытки входа в указанном временном диапазоне.
>>>>>>> main:auth-statistics-service/src/main/java/ru/katacademy/domain/repository/JpaLoginAttemptAuthRepository.java
     * @param userId идентификатор пользователя (не может быть {@code null})
     * @param start начальная дата диапазона
     * @param end   конечная дата диапазона
     * @return список попыток входа или пустой список, если ничего не найдено
     */
    List<LoginAttemptEntity> findByUserIdAndTimestampBetween(Long userId, LocalDateTime start, LocalDateTime end);

    /**
     * Находит все попытки входа по указанному результату.
     *
     * @param userId идентификатор пользователя (не может быть {@code null})
     * @param success результат попытки:
     *                {@code true} - успешные,
     *                {@code false} - неудачные
     * @return список попыток входа или пустой список, если ничего не найдено
     */
    List<LoginAttemptEntity> findByUserIdAndSuccess(Long userId, Boolean success);

    /**
     * Находит все попытки входа в указанном временном диапазоне и результату.
     *
     * @param userId идентификатор пользователя (не может быть {@code null})
     * @param start начальная дата диапазона
     * @param end   конечная дата диапазона
     * @param success результат попытки:
     *                {@code true} - успешные,
     *                {@code false} - неудачные
     * @return список попыток входа или пустой список, если ничего не найдено
     */
    List<LoginAttemptEntity> findByUserIdAndTimestampBetweenAndSuccess(Long userId, LocalDateTime start, LocalDateTime end, Boolean success);
}
