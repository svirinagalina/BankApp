package ru.katacademy.bank_app.accountservice.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.katacademy.bank_app.accountservice.application.command.ChangePasswordCommand;
import ru.katacademy.bank_app.accountservice.application.dto.PasswordChangedEvent;
import ru.katacademy.bank_app.accountservice.application.dto.RegisterUserCommand;
import ru.katacademy.bank_app.accountservice.application.dto.UserDto;
import ru.katacademy.bank_app.accountservice.application.port.out.UserRepository;
import ru.katacademy.bank_app.accountservice.domain.entity.User;
import ru.katacademy.bank_app.accountservice.domain.factory.UserFactory;
import ru.katacademy.bank_app.accountservice.domain.mapper.UserMapper;
import ru.katacademy.bank_app.accountservice.domain.service.UserService;
import ru.katacademy.bank_app.accountservice.infrastructure.client.KycClient;
import ru.katacademy.bank_app.accountservice.infrastructure.messaging.PasswordChangeEventPublisher;
import ru.katacademy.bank_app.audit.annotation.Auditable;
import ru.katacademy.bank_shared.exception.*;
import ru.katacademy.bank_shared.valueobject.Email;

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
    private final PasswordChangeEventPublisher passwordChangeEventPublisher;
    private final KycClient kycClient;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordChangeEventPublisher passwordChangeEventPublisher, KycClient kycClient
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordChangeEventPublisher = passwordChangeEventPublisher;
        this.kycClient = kycClient;
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
    @Auditable(action = "Регистрация аккаунта")
    @Transactional
    @Override
    public UserDto register(RegisterUserCommand cmd) throws DomainException {

        final Optional<User> existingUser = userRepository.findByEmail(new Email(cmd.email()));
        if (existingUser.isPresent()) {
            throw new EmailAlreadyTakenException(cmd.email());
        }



        if (!isValidPassword(cmd.password())) {
            throw new InvalidPasswordException(
                    "Пароль должен состоять не менее чем из 8 символов, " +
                            "а также содержать латинские буквы и числа от 0 до 9"
            );
        }

        final User newUser = UserFactory.create(cmd);
        final User savedUser = userRepository.save(newUser);

        final var kyc = kycClient.getKyc(savedUser.getId());
        if (kyc == null || !kyc.status().isApproved()) {
            throw new KycException("User is not KYC-verified");
        }
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
     * <p>Метод выполняет следующие действия:</p>
     * <ol>
     *     <li>Поиск пользователя по идентификатору.</li>
     *     <li>Проверка текущего пароля на соответствие.</li>
     *     <li>Проверка нового пароля на отличие от старого.</li>
     *     <li>Валидация нового пароля по заданным критериям.</li>
     *     <li>Установка нового пароля и его хеширование.</li>
     *     <li>Сохранение изменённого пользователя в репозитории.</li>
     *     <li>Публикация события о смене пароля.</li>
     * </ol>
     *
     * @param command Команда, содержащая идентификатор пользователя и пароли.
     * @throws UserNotFoundException    Если пользователь с указанным идентификатором не найден.
     * @throws InvalidPasswordException Если текущий пароль не совпадает с хешем. Если новый пароль совпадает с текущим.
     *                                  Если новый пароль не соответствует критериям (менее 8 символов,
     *                                  Если не содержит латинские буквы и цифры от 0 до 9).
     */
    @Auditable(action = "Смена пароля")
    @Transactional
    public void changePassword(ChangePasswordCommand command) {
        final User user = userRepository
                .findById(command.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        // Проверяем текущий пароль
        if (!BCrypt.checkpw(command.getOldPassword(), user.getPasswordHash())) {
            throw new InvalidPasswordException("Текущий пароль некорректный");
        }

        // Проверяем, что новый пароль отличается от старого
        if (BCrypt.checkpw(command.getNewPassword(), user.getPasswordHash())) {
            throw new InvalidPasswordException("Новый пароль должен отличаться от старого");
        }

        // Проверяем новый пароль на валидность
        if (!isValidPassword(command.getNewPassword())) {
            throw new InvalidPasswordException("Пароль должен состоять не менее чем из 8 символов, " +
                    "а также содержать латинские буквы и числа от 0 до 9");
        }

        // Сохраняем старый хеш до изменений
        final String oldPasswordHash = user.getPasswordHash();

        // Устанавливаем и сохраняем новый пароль
        final String newEnteredPasswordHash = BCrypt.hashpw(command.getNewPassword(), BCrypt.gensalt());
        user.setPasswordHash(newEnteredPasswordHash);
        userRepository.save(user);

        // Публикуем событие о смене пароля
        final PasswordChangedEvent event = new PasswordChangedEvent(
                user.getId(),
                oldPasswordHash,  // значение пароля до изменений
                user.getPasswordHash()  // передаем обновленное значение
        );
        passwordChangeEventPublisher.publish(event);
    }


    /**
     * Вспомогательный метод для changePassword().
     * <p>
     * Проверяет, что указанный пароль является валидным.
     * <p>
     * Этот метод проверяет, соответствует ли пароль следующим критериям:
     * - Должен содержать не менее 8 символов.
     * - Должен включать как минимум одну латинскую букву (верхнего или нижнего регистра).
     * - Должен содержать как минимум одну цифру от 0 до 9.
     *
     * @param input строка, представляющая пароль для проверки. Не может быть null.
     * @return true, если пароль соответствует критериям; иначе false.
     * @throws IllegalArgumentException если input является null.
     *                                  <p>
     *                                  Автор: Колпаков А.С..
     *                                  Дата: 2025-04-30
     */
    private static boolean isValidPassword(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Пароль не может быть Null");
        }

        final String regex = "^(?=.*[0-9])(?=.*[a-zA-Z])[a-zA-Z0-9]{8,}$";
        return input.matches(regex);
    }
}