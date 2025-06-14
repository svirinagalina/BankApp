package ru.katacademy.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.katacademy.domain.entity.LoginAttempt;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Репозиторий для работы с попытками входа в систему в базе данных.
 * <p>
 * Предоставляет методы для выборки записей из таблицы {@code auth_login_attempts}.
 * </p>
 *
 * @author MihasBatler
 */
public interface LoginAttemptAuthRepository extends JpaRepository<LoginAttempt, Long> {

    /**
     * Находит все попытки входа по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя (не может быть {@code null})
     * @return список попыток входа или пустой список, если ничего не найдено
     */
    List<LoginAttempt> findByUserId(Long userId);

    /**
     * Находит все попытки входа в указанном временном диапазоне.
     *
     * @param start начальная дата диапазона
     * @param end   конечная дата диапазона
     * @return список попыток входа или пустой список, если ничего не найдено
     */
    List<LoginAttempt> findByTimestamp(LocalDateTime start, LocalDateTime end);

    /**
     * Находит все попытки входа по указанному результату.
     *
     * @param success результат попытки:
     *                {@code true} - успешные,
     *                {@code false} - неудачные
     * @return список попыток входа или пустой список, если ничего не найдено
     */
    List<LoginAttempt> findBySuccess(boolean success);
}
