package ru.katacademy.bank_app.user.application.service;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.katacademy.bank_app.shared.exception.DomainException;
import ru.katacademy.bank_app.shared.exception.EmailAlreadyTakenException;
import ru.katacademy.bank_app.shared.valueobject.Email;
import ru.katacademy.bank_app.user.application.command.ChangePasswordCommand;
import ru.katacademy.bank_app.user.domain.repository.UserRepository;
import ru.katacademy.bank_app.shared.exception.UserNotFoundException;
import ru.katacademy.bank_app.user.application.dto.RegisterUserCommand;
import ru.katacademy.bank_app.user.application.dto.UserDto;
import ru.katacademy.bank_app.user.domain.entity.User;
import ru.katacademy.bank_app.user.domain.factory.UserFactory;
import ru.katacademy.bank_app.user.domain.mapper.UserMapper;

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

    /**
     * Меняет пароль пользователя.
     *
     * Метод проверяет, соответствует ли введенный текущий пароль
     * и что новый пароль отличается от старого. Также проверяется, что
     * новый пароль соответствует установленным критериям валидности.
     *
     * @param command объект команды для смены пароля, содержащий идентификатор пользователя,
     *                старый пароль и новый пароль. Не может быть null.
     * @throws RuntimeException если пользователь не найден.
     *                          если текущий пароль некорректный.
     *                          если новый пароль совпадает с текущим.
     *                          если новый пароль не соответствует требованиям валидности.
     *
     *  Автор: Колпаков А.С..
     *  Дата: 2025-04-30
     */
    @Transactional
    public void changePassword(ChangePasswordCommand command) {
        final User user = userRepository
                .findById(command.getUserId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        final String oldEnteredPasswordHash = BCrypt.hashpw(command.getOldPassword(), BCrypt.gensalt());
        final String newEnteredPasswordHash = BCrypt.hashpw(command.getNewPassword(), BCrypt.gensalt());

        // проверяем текущий пароль
        if (!BCrypt.checkpw(oldEnteredPasswordHash, user.getPasswordHash())) {
            throw new RuntimeException("Текущий пароль некорректный");
        }

        // проверяем, что новый пароль отличается от старого
        if (BCrypt.checkpw(newEnteredPasswordHash, user.getPasswordHash())) {
            throw new RuntimeException("Новый пароль должен отличаться от старого");
        }

        // проверяем новый пароль на валидность
        if (!isValidPassword(command.getNewPassword())) {
            throw new RuntimeException("Пароль должен состоять не менее чем из 6 символов, а также содержать латинские буквы и числа от 0 до 9");
        }

        // устанавливаем и сохраняем новый пароль
        user.setPasswordHash(newEnteredPasswordHash);
        userRepository.save(user);
    }

    /**
     * Вспомогательный метод для changePassword().
     *
     * Проверяет, что указанный пароль является валидным.
     *
     * Этот метод проверяет, соответствует ли пароль следующим критериям:
     * - Должен содержать не менее 6 символов.
     * - Должен включать как минимум одну латинскую букву (верхнего или нижнего регистра).
     * - Должен содержать как минимум одну цифру от 0 до 9.
     *
     * @param input строка, представляющая пароль для проверки. Не может быть null.
     * @return true, если пароль соответствует критериям; иначе false.
     * @throws IllegalArgumentException если input является null.
     *
     * Автор: Колпаков А.С..
     * Дата: 2025-04-30
     */
    private static boolean isValidPassword(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Пароль не может быть Null");
        }

        final String regex = "^(?=.*[0-9])(?=.*[a-zA-Z])[a-zA-Z0-9]{8,}$";
        return input.matches(regex);
    }
}