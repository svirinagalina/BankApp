package ru.katacademy.bank_app.settingsservice.application.port.out;

import ru.katacademy.bank_app.settingsservice.domain.entity.UserSettings;

import java.util.Optional;

/**
 * Порт выхода для работы с пользовательскими настройками.
 * Определяет CRUD‑операции над доменной сущностью {@link UserSettings}.
 */
public interface UserSettingsRepository {

    /**
     * Находит настройки пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя
     * @return {@link Optional} с найденными настройками или пустой, если их нет
     */
    Optional<UserSettings> findById(Long userId);

    /**
     * Сохраняет или обновляет настройки пользователя.
     *
     * @param settings объект {@link UserSettings} для сохранения
     * @return сохранённый объект с заполненными автоматически полями (например, createdAt/updatedAt)
     */
    UserSettings save(UserSettings settings);

    /**
     * Удаляет настройки пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя, чьи настройки нужно удалить
     */
    void deleteById(Long userId);
}
