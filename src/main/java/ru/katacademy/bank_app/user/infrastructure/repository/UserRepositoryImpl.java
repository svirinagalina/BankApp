package ru.katacademy.bank_app.user.infrastructure.repository;

import org.springframework.stereotype.Repository;

import ru.katacademy.bank_app.user.domain.repository.UserRepository;
import ru.katacademy.bank_app.shared.valueobject.Email;
import ru.katacademy.bank_app.user.domain.entity.User;

import java.util.Optional;

/**
 * Реализация репозитория пользователей.
 * Использует JpaUserRepository для взаимодействия с базой данных через Spring Data JPA.
 * Обеспечивает методы для поиска пользователей по ID и email, а также сохранение пользователей.
 * <p>
 * Поля:
 * - jpaRepository: репозиторий JPA для работы с сущностью User.
 * <p>
 * Методы:
 * - findById(Long id): находит пользователя по его идентификатору.
 * - findByEmail(Email email): находит пользователя по его email.
 * - save(User user): сохраняет пользователя в базе данных.
 * <p>
 * Автор: Бачагов В.О.
 * Дата: 2024-04-17
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaRepository;

    /**
     * Конструктор, инициализирующий репозиторий.
     *
     * @param jpaRepository репозиторий JPA для работы с User.
     */
    public UserRepositoryImpl(JpaUserRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    /**
     * Находит пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя.
     * @return объект Optional с найденным пользователем, если он существует.
     */
    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id);
    }

    /**
     * Находит пользователя по его email.
     *
     * @param email email пользователя.
     * @return объект Optional с найденным пользователем, если он существует.
     */
    @Override
    public Optional<User> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.value());
    }

    /**
     * Сохраняет пользователя в базе данных.
     *
     * @param user объект пользователя, который нужно сохранить.
     * @return сохранённый пользователь.
     */
    @Override
    public User save(User user) {
        return jpaRepository.save(user);
    }
}