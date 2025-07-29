package ru.katacademy.bank_app.accountservice.application.port.out;

import ru.katacademy.bank_app.accountservice.domain.entity.User;
import ru.katacademy.bank_shared.valueobject.Email;
import java.util.Optional;

/**
 * Порт выхода для работы с сущностями пользователей.
 * CRUD‑операции над {@link User}.
 */
public interface UserRepository {

    /**
     * Ищет пользователя по идентификатору.
     *
     * @param id уникальный идентификатор пользователя
     * @return Optional с найденным пользователем или пустой Optional, если не найден
     */
    Optional<User> findById(Long id);

    /**
     * Ищет пользователя по электронной почте.
     *
     * @param email объект {@link Email}, используемый для поиска
     * @return Optional с найденным пользователем или пустой Optional, если не найден
     */
    Optional<User> findByEmail(Email email);

    /**
     * Сохраняет нового пользователя или обновляет существующего.
     *
     * @param user объект {@link User} для сохранения
     * @return сохранённый пользователь с заполненным полем {@code id}
     */
    User save(User user);
}
