package ru.katacademy.bank_app.user.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.katacademy.bank_app.user.application.port.UserRepository;
import ru.katacademy.bank_app.shared.exception.UserNotFoundException;
import ru.katacademy.bank_app.shared.valueobject.Email;
import ru.katacademy.bank_app.user.application.dto.RegisterUserCommand;
import ru.katacademy.bank_app.user.application.dto.UserDto;
import ru.katacademy.bank_app.user.domain.entity.User;
import ru.katacademy.bank_app.user.domain.enumtype.UserRole;

import java.time.LocalDateTime;

/**
 * Реализация сервиса пользователей.
 * Обрабатывает регистрацию и получение пользователя через репозиторий.
 * <p>
 * Поля:
 * - userRepository: репозиторий для доступа к данным пользователей
 * <p>
 * Методы:
 * - register(): регистрирует нового пользователя
 * - getById(): получает пользователя по ID
 * <p>
 * Автор: Бачагов В.О.
 * Дата: 2025-04-17
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Регистрирует нового пользователя.
     * Проверяет, что email не занят, и сохраняет нового пользователя.
     *
     * @param cmd команда с данными пользователя
     * @return DTO нового пользователя
     * @throws IllegalArgumentException если email уже существует
     */
    @Transactional
    @Override
    public UserDto register(RegisterUserCommand cmd) {
        Email email = new Email(cmd.email());

        userRepository.findByEmail(email).ifPresent(existingUser -> {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        });

        User user = new User(
                null,
                UserRole.USER,
                cmd.fullName(),
                email,
                cmd.password(),
                LocalDateTime.now()
        );

        User savedUser = userRepository.save(user);

        return new UserDto(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail().value(),
                savedUser.getRole()
        );
    }

    /**
     * Получает пользователя по ID.
     *
     * @param id идентификатор пользователя
     * @return DTO найденного пользователя
     * @throws UserNotFoundException если пользователь не найден
     */
    @Transactional(readOnly = true)
    @Override
    public UserDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + id + " не найден"));

        return new UserDto(
                user.getId(),
                user.getFullName(),
                user.getEmail().value(),
                user.getRole()
        );
    }
}