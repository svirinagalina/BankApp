package ru.katacademy.bank_app.user.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.katacademy.bank_app.user.domain.entity.User;
import ru.katacademy.bank_app.user.infrastructure.persistence.entity.UserEntity;

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
public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Находит пользователя по email.
     *
     * @param email email пользователя
     * @return Optional<User> найденного пользователя
     */
    Optional<UserEntity> findByEmail(String email);
}