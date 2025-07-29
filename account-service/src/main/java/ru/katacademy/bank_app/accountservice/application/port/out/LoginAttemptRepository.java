package ru.katacademy.bank_app.accountservice.application.port.out;

import ru.katacademy.bank_app.accountservice.domain.entity.LoginAttemptEntry;
import java.util.Optional;

/**
 * Порт выхода для работы с записями попыток входа.
 * Предоставляет CRUD‑операции над {@link LoginAttemptEntry}.
 */
public interface LoginAttemptRepository {

    /**
     * Ищет запись попытки входа по её идентификатору.
     *
     * @param id уникальный идентификатор записи
     * @return Optional с найденной записью или пустой Optional, если запись не найдена
     */
    Optional<LoginAttemptEntry> findById(Long id);

    /**
     * Сохраняет новую или обновляет существующую запись попытки входа.
     *
     * @param attempt объект {@link LoginAttemptEntry} для сохранения
     * @return сохранённая сущность с заполненным полем {@code id}
     */
    LoginAttemptEntry save(LoginAttemptEntry attempt);
}
