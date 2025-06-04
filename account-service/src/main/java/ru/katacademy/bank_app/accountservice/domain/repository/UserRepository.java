package ru.katacademy.bank_app.accountservice.domain.repository;

import ru.katacademy.bank_shared.valueobject.Email;
import ru.katacademy.bank_app.accountservice.domain.entity.User;

import java.util.Optional;

/**
 * Интерфейс репозитория пользователей.
 * Определяет операции доступа к данным пользователей.
 * <p>
 * Методы:
 * - findById(): поиск пользователя по ID
 * - findByEmail(): поиск пользователя по email
 * - save(): сохранение пользователя
 * <p>
 * Автор: Бачагов В.О.
 * Дата: 2025-04-17
 */
public interface UserRepository {

    /**
     * Ищет пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return Optional с найденным пользователем или пустой, если не найден
     */
    Optional<User> findById(Long id);

    /**
     * Ищет пользователя по email.
     *
     * @param email email-объект
     * @return Optional с найденным пользователем или пустой, если не найден
     */
    Optional<User> findByEmail(Email email);

    /**
     * Сохраняет пользователя.
     *
     * @param user пользователь для сохранения
     * @return сохранённый пользователь
     */
    User save(User user);
}