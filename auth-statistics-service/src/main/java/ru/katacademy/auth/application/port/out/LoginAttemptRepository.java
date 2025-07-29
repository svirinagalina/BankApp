package ru.katacademy.auth.application.port.out;

import ru.katacademy.auth.domain.entity.LoginAttempt;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Порт выхода для работы с логами попыток входа.
 * Определяет CRUD‑операции над доменной сущностью {@link LoginAttempt}.
 */
public interface LoginAttemptRepository {

    /**
     * Возвращает все попытки входа для заданного пользователя.
     *
     * @param userId идентификатор пользователя (не может быть {@code null})
     * @return список найденных попыток входа, или пустой список, если ничего не найдено
     */
    List<LoginAttempt> findByUserId(Long userId);

    /**
     * Возвращает все попытки входа за указанный период.
     *
     * @param start начало временного диапазона (включительно)
     * @param end   конец временного диапазона (включительно)
     * @return список найденных попыток входа, или пустой список, если ничего не найдено
     */
    List<LoginAttempt> findByTimestamp(LocalDateTime start, LocalDateTime end);

    /**
     * Возвращает все попытки входа по их успешности.
     *
     * @param success {@code true} для успешных попыток, {@code false} — для неудачных
     * @return список попыток входа с указанным статусом успеха
     */
    List<LoginAttempt> findBySuccess(boolean success);

    /**
     * Сохраняет или обновляет информацию о попытке входа.
     *
     * @param attempt доменный объект {@link LoginAttempt} для сохранения
     * @return сохранённый объект с заполненным полем {@code id} и другими автоматически заполняемыми полями
     */
    LoginAttempt save(LoginAttempt attempt);
}
