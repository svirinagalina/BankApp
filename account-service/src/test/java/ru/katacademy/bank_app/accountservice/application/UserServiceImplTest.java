package ru.katacademy.bank_app.accountservice.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCrypt;
import ru.katacademy.bank_app.accountservice.application.command.ChangePasswordCommand;
import ru.katacademy.bank_app.accountservice.application.dto.KycRequestDTO;
import ru.katacademy.bank_app.accountservice.application.dto.PasswordChangedEvent;
import ru.katacademy.bank_app.accountservice.application.dto.RegisterUserCommand;
import ru.katacademy.bank_app.accountservice.application.dto.UserDto;
import ru.katacademy.bank_app.accountservice.application.port.out.UserRepository;
import ru.katacademy.bank_app.accountservice.application.service.UserServiceImpl;
import ru.katacademy.bank_app.accountservice.domain.entity.User;
import ru.katacademy.bank_shared.enums.KycStatus;
import ru.katacademy.bank_app.accountservice.domain.enumtype.UserRole;
import ru.katacademy.bank_app.accountservice.domain.mapper.UserMapper;
import ru.katacademy.bank_shared.exception.KycServiceUnavailableException;
import ru.katacademy.bank_app.accountservice.infrastructure.client.KycClient;
import ru.katacademy.bank_app.accountservice.infrastructure.messaging.PasswordChangeEventPublisher;
import ru.katacademy.bank_shared.exception.DomainException;
import ru.katacademy.bank_shared.exception.EmailAlreadyTakenException;
import ru.katacademy.bank_shared.exception.InvalidPasswordException;
import ru.katacademy.bank_shared.exception.UserNotFoundException;
import ru.katacademy.bank_shared.valueobject.Email;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Тест проверяет работу методов UserServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordChangeEventPublisher eventPublisher;

    @Mock
    private KycClient kycClient;

    @InjectMocks
    private UserServiceImpl userService;

    private final String fullName = "Тест Тестович Тестов";
    private final String email = "test@mail.ru";
    private final String password = "password123";

    // Проверяем добавление нового пользователя
    @Test
    void register_ShouldReturnUserDto_WhenValidCommandProvided() throws DomainException {
        final RegisterUserCommand cmd = new RegisterUserCommand(fullName, email, password);

        final User newUser = new User(UserRole.USER, cmd.fullName(), new Email(cmd.email()),
                cmd.password(), LocalDateTime.now());

        final User savedUser = new User(1L, newUser.getRole(), newUser.getFullName(),
                newUser.getEmail(), newUser.getPasswordHash(),
                newUser.getCreatedAt());

        final UserDto expectedDto = new UserDto(1L, fullName, email, UserRole.USER);

        final KycRequestDTO kycRequestDTO = new KycRequestDTO(1L, KycStatus.APPROVED);

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(expectedDto);
        when(kycClient.getKyc(anyLong())).thenReturn(kycRequestDTO);


        final UserDto result = userService.register(cmd);

        assertThat(result).isEqualTo(expectedDto);
    }

    // проверяем, что если пользователь с таким email уже существует, выбрасываем исключение
    @Test
    void register_ShouldThrowEmailAlreadyTakenException_WhenEmailExists() {
        final RegisterUserCommand cmd = new RegisterUserCommand(fullName, email, password);

        final User existingUser = new User(UserRole.USER, fullName,
                new Email(email), password, LocalDateTime.now());

        when(userRepository.findByEmail(any(Email.class)))
                .thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.register(cmd))
                .isInstanceOf(EmailAlreadyTakenException.class)
                .hasMessageContaining(email);

        verify(userRepository).findByEmail(new Email(email));
        verify(userRepository, never()).save(any());
    }

    // проверка получения пользователя по идентификатору
    @Test
    void getById_ShouldReturnUserDto_WhenUserExists() {
        // Arrange
        final Long userId = 1L;
        final String testEmail = "test@example.com";  // Явно задаем email
        final String testFullName = "Test User";     // Явно задаем fullName
        final UserRole testRole = UserRole.USER;     // Явно задаем роль

        final User user = new User(
                userId,
                testRole,
                testFullName,
                new Email(testEmail),  // Создаем Email объект
                "hashed_password",
                LocalDateTime.now()
        );

        final UserDto expectedDto = new UserDto(
                userId,
                testFullName,
                testEmail,  // Должно совпадать с user.getEmail().value()
                testRole
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(expectedDto);

        final UserDto result = userService.getById(userId);

        assertThat(result.id()).isEqualTo(expectedDto.id());
        assertThat(result.fullName()).isEqualTo(expectedDto.fullName());
        assertThat(result.email()).isEqualTo(expectedDto.email());
        assertThat(result.role()).isEqualTo(expectedDto.role());

        verify(userRepository).findById(userId);
        verify(userMapper).toDto(user);
    }

    // проверка правильности выбрасываемого исключения при не нахождении пользователя по id
    @Test
    void getById_ShouldThrowUserNotFoundException_WhenUserNotExists() {
        final Long nonExistentId = 999L;
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> userService.getById(nonExistentId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Пользователь с id " + nonExistentId + " не найден");

        verify(userRepository).findById(nonExistentId);
    }

    // проверка отправки события в kafka при смене пароля
    @Test
    void changePassword_ShouldUpdatePasswordAndPublishEvent_WhenValidData() {
        final Long userId = 1L;
        final String oldPassword = password;
        final String newPassword = "newValid123";
        final String oldHash = BCrypt.hashpw(oldPassword, BCrypt.gensalt());

        final User user = new User(userId, UserRole.USER, fullName,
                new Email(email), oldHash, LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.changePassword(new ChangePasswordCommand(userId, oldPassword, newPassword));

        verify(userRepository).save(user);
        assertThat(BCrypt.checkpw(newPassword, user.getPasswordHash())).isTrue();
        verify(eventPublisher).publish(any(PasswordChangedEvent.class));
    }

    // Тест проверяет, что метод changePassword() выбрасывает исключение InvalidPasswordException,
    // когда пользователь пытается изменить пароль, но вводит неправильный старый пароль.
    @Test
    void changePassword_ShouldThrowException_WhenOldPasswordIncorrect() {
        final Long userId = 1L;
        final User user = new User(userId, UserRole.USER, fullName,
                new Email(email), BCrypt.hashpw(password, BCrypt.gensalt()), LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThatThrownBy(() ->
                userService.changePassword(new ChangePasswordCommand(userId, "wrong", "newPass123")))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("Текущий пароль некорректный");
    }

    // Тест проверяет, совпадает ли новый пароль со старым
    @Test
    void changePassword_ShouldThrowException_WhenNewPasswordSameAsOld() {
        final Long userId = 1L;
        final String password = "SamePassword123";
        final String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());

        final User user = new User(userId, UserRole.USER, fullName,
                new Email(email), passwordHash, LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        final ChangePasswordCommand cmd = new ChangePasswordCommand(userId, password, password);

        assertThatThrownBy(() -> userService.changePassword(cmd))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("Новый пароль должен отличаться от старого");
    }

    // Тест проверяет валидность нового пароля на содержание цифр, если цифр и(или) символов меньше 8 выбросит исключение
    @Test
    void changePassword_ShouldThrowException_WhenNewPasswordIsInvalid() {
        final Long userId = 1L;
        final String oldPassword = "OldPass123";
        final String newPassword = "invalid";

        final User user = new User(userId, UserRole.USER, fullName,
                new Email(email), BCrypt.hashpw(oldPassword, BCrypt.gensalt()), LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        final ChangePasswordCommand cmd = new ChangePasswordCommand(userId, oldPassword, newPassword);

        assertThatThrownBy(() -> userService.changePassword(cmd))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("Пароль должен состоять не менее чем из 8 символов");
    }

    @Test
    void register_success_whenKycApproved() throws DomainException {

        final var command = new RegisterUserCommand("Test Testov", "approved@test.com", "Password123");

        // Мокаем поиск пользователя по email
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        // Мокаем сохранение пользователя, присваивая ID
        final User savedUser = new User(
                1L,
                UserRole.USER,
                command.fullName(),
                new Email(command.email()),
                "hashedPassword",
                LocalDateTime.now()
        );
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Мокаем KYC
        when(kycClient.getKyc(1L)).thenReturn(new KycRequestDTO(1L, KycStatus.APPROVED));

        // Мокаем маппинг User -> UserDto
        final UserDto userDtoMock = new UserDto(
                1L,
                command.fullName(),
                command.email(),
                UserRole.USER
        );
        when(userMapper.toDto(savedUser)).thenReturn(userDtoMock);

        // Вызов сервиса
        final var userDto = userService.register(command);

        // Проверки
        assertNotNull(userDto);
        assertEquals(1L, userDto.id());
        assertEquals("approved@test.com", userDto.email());
        assertEquals(UserRole.USER, userDto.role());
    }

    @Test
    void register_fails_whenKycRejected() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(kycClient.getKyc(null)).thenReturn(new KycRequestDTO(1L, KycStatus.REJECTED));

        final var command = new RegisterUserCommand("Test Testov", "rejected@test.com", "Password123");

        final DomainException ex = assertThrows(DomainException.class, () -> userService.register(command));
        assert ex.getMessage().equals("User is not KYC-verified");
    }

    @Test
    void register_fails_whenKycServiceUnavailable() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(kycClient.getKyc(null)).thenThrow(new KycServiceUnavailableException("Verification service temporarily unavailable"));

        final var command = new RegisterUserCommand("Test Testov", "fail@test.com", "Password123");

        final KycServiceUnavailableException ex = assertThrows(KycServiceUnavailableException.class,
                () -> userService.register(command));
        assert ex.getMessage().equals("Verification service temporarily unavailable");
    }
}