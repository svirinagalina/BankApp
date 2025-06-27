package ru.katacademy.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.katacademy.infrastructure.entity.JpaLoginAttempt;
import ru.katacademy.infrastructure.repository.LoginAttemptAuthRepositoryImpl;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA-репозиторий для работы с попытками входа в систему.
 * <p>
 * Предоставляет стандартные CRUD-операции через {@link JpaRepository}
 * и кастомные запросы для сущности {@link JpaLoginAttempt}.
 * Все методы работают с JPA-сущностями, преобразование в domain-объекты
 * выполняется в {@link LoginAttemptAuthRepositoryImpl}.
 * Работает напрямую с таблицей {@code auth_login_attempts}
 * </p>
 *
 * @author MihasBatler
 * @see JpaLoginAttempt Сущность, с которой работает репозиторий
 */
public interface JpaLoginAttemptAuthRepository extends JpaRepository<JpaLoginAttempt, Long> {

    /**
     * Находит все попытки входа по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя (не может быть {@code null})
     * @return список попыток входа или пустой список, если ничего не найдено
     */
    List<JpaLoginAttempt> findByUserId(Long userId);

    /**
     * Находит все попытки входа в указанном временном диапазоне.
     *
     * @param start начальная дата диапазона
     * @param end   конечная дата диапазона
     * @return список попыток входа или пустой список, если ничего не найдено
     */
    List<JpaLoginAttempt> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Находит все попытки входа по указанному результату.
     *
     * @param success результат попытки:
     *                {@code true} - успешные,
     *                {@code false} - неудачные
     * @return список попыток входа или пустой список, если ничего не найдено
     */
    List<JpaLoginAttempt> findBySuccess(boolean success);
}
