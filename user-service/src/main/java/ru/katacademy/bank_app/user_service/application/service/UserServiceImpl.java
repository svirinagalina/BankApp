package ru.katacademy.bank_app.user_service.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.katacademy.bank_shared.exception.DomainException;
import ru.katacademy.bank_shared.exception.EmailAlreadyTakenException;
import ru.katacademy.bank_shared.valueobject.Email;
import ru.katacademy.bank_app.user_service.domain.repository.UserRepository;
import ru.katacademy.bank_shared.exception.UserNotFoundException;
import ru.katacademy.bank_app.user_service.application.dto.RegisterUserCommand;
import ru.katacademy.bank_app.user_service.application.dto.UserDto;
import ru.katacademy.bank_app.user_service.domain.entity.User;
import ru.katacademy.bank_app.user_service.domain.factory.UserFactory;
import ru.katacademy.bank_app.user_service.domain.mapper.UserMapper;

import java.util.Optional;

/**
 * Реализация сервиса пользователей.
 * Обрабатывает регистрацию и получение пользователя через репозиторий.
 * <p>
 * Поля:
 * - userRepository: репозиторий для доступа к данным пользователей
 * - userMapper: маппер для преобразования User в UserDto
 * <p>
 * Методы:
 * - register(): регистрирует нового пользователя
 * - getById(): получает пользователя по ID
 * <p>
 * Автор: Бачагов В.О.
 * Дата: 2025-04-18
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    /**
     * Регистрирует нового пользователя.
     * <p>
     * Выполняет проверку уникальности email, создание нового пользователя через
     * фабрику и сохранение в базе данных.
     *
     * @param cmd команда с данными для регистрации
     * @return DTO пользователя после успешной регистрации
     * @throws EmailAlreadyTakenException если email уже используется
     * @throws DomainException            если произошла ошибка при регистрации
     */
    @Transactional
    @Override
    public UserDto register(RegisterUserCommand cmd) throws DomainException {
        final Optional<User> existingUser = userRepository.findByEmail(new Email(cmd.email()));
        if (existingUser.isPresent()) {
            throw new EmailAlreadyTakenException(cmd.email());
        }
        final User newUser = UserFactory.create(cmd);
        final User savedUser = userRepository.save(newUser);

        return userMapper.toDto(savedUser);
    }

    /**
     * Получает пользователя по ID.
     * <p>
     * Если пользователь не найден, выбрасывает исключение {@link UserNotFoundException}.
     * <p>
     *
     * @param id идентификатор пользователя
     * @return DTO найденного пользователя
     * @throws UserNotFoundException если пользователь с таким ID не найден
     */
    @Transactional(readOnly = true)
    @Override
    public UserDto getById(Long id) {
        final User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + id + " не найден"));

        return userMapper.toDto(user);
    }
}