package ru.katacademy.bank_app.account.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.katacademy.bank_app.user.domain.entity.User;

import java.util.Optional;

/**
 * Репозиторий для работы с пользователями.
 * Наследует интерфейс JpaRepository и предоставляет стандартные методы для работы с базой данных.
 * <p>
 * Методы:
 * - findByEmail(): находит пользователя по email
 * <p>
 * Автор: Бачагов В.О.
 * Дата: 2025-04-17
 */
public interface JpaUserRepository extends JpaRepository<User, Long> {

    /**
     * Находит пользователя по email.
     *
     * @param email email пользователя
     * @return Optional<User> найденного пользователя
     */
    Optional<User> findByEmail(String email);
}