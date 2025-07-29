package ru.katacademy.auth.domain.repository;

import ru.katacademy.auth.domain.entity.LoginAttempt;

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
public interface LoginAttemptAuthRepository {

    /**
     * Находит все попытки входа по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя (не может быть {@code null})
     * @return список попыток входа или пустой список, если ничего не найдено
     */
    List<LoginAttempt> findByUserId(Long userId);

    /**
     * Находит все попытки входа по идентификатору пользователя в указанном временном диапазоне.
     *
     * @param userId идентификатор пользователя
     * @param start  начальная дата диапазона
     * @param end    конечная дата диапазона
     * @return список попыток входа или пустой список, если ничего не найдено
     */
    List<LoginAttempt> findByUserIdAndTimestampBetween(Long userId, LocalDateTime start, LocalDateTime end);

    /**
     * Находит все попытки входа по идентификатору пользователя и результату.
     *
     * @param userId  идентификатор пользователя
     * @param success результат попытки:
     *                {@code true} - успешные,
     *                {@code false} - неудачные
     * @return список попыток входа или пустой список, если ничего не найдено
     */
    List<LoginAttempt> findByUserIdAndSuccess(Long userId, Boolean success);

    /**
     * Находит все попытки входа по идентификатору пользователя, в указанном временном диапазоне и с указанным результатом.
     *
     * @param userId  идентификатор пользователя
     * @param start   начальная дата диапазона
     * @param end     конечная дата диапазона
     * @param success результат попытки:
     *                {@code true} - успешные,
     *                {@code false} - неудачные
     * @return список попыток входа или пустой список, если ничего не найдено
     */
    List<LoginAttempt> findByUserIdAndTimestampBetweenAndSuccess(Long userId, LocalDateTime start, LocalDateTime end, Boolean success);

    List<LoginAttempt> findByTimestamp(LocalDateTime start, LocalDateTime end);

    List<LoginAttempt> findBySuccess(boolean success);

    /**
     * Сохраняет или обновляет информацию о попытке входа.
     *
     * @param attempt объект попытки входа для сохранения
     * @return сохраненный объект с заполненными полями
     */
    LoginAttempt save(LoginAttempt attempt);
}
