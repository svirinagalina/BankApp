package ru.katacademy.bank_app.accountservice.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCrypt;
import ru.katacademy.bank_app.accountservice.application.command.ChangePasswordCommand;
import ru.katacademy.bank_app.accountservice.application.dto.PasswordChangedEvent;
import ru.katacademy.bank_app.accountservice.domain.entity.User;
import ru.katacademy.bank_app.accountservice.domain.enumtype.UserRole;
import ru.katacademy.bank_app.accountservice.domain.mapper.UserMapper;
import ru.katacademy.bank_app.accountservice.application.port.out.UserRepository;
import ru.katacademy.bank_app.accountservice.infrastructure.messaging.PasswordChangeEventPublisher;
import ru.katacademy.bank_shared.valueobject.Email;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit-тест для проверки корректности формирования события смены пароля.
 *
 * <p>
 * Проверяет, что при смене пароля:
 * <ul>
 *     <li>В событие PasswordChangedEvent передаётся старый хеш пароля до изменения</li>
 *     <li>В событие PasswordChangedEvent передаётся новый (обновлённый) хеш</li>
 *     <li>Оба хеша не совпадают</li>
 * </ul>
 * </p>
 */
public class ChangePasswordUserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordChangeEventPublisher passwordChangeEventPublisher;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Проверяет, что событие смены пароля содержит старый и новый хеши пароля.
     * <ul>
     *     <li>Старый хеш соответствует хешу пароля до изменения</li>
     *     <li>Новый хеш — хеш нового пароля</li>
     *     <li>Они не совпадают</li>
     * </ul>
     */
    @Test
    void testChangePassword_PasswordChangedEventContainsOldAndNewHash() {
        final Long userId = 1L;
        final String oldPassword = "OldPassword123";
        final String newPassword = "newPassword123";
        final String oldPasswordHash = BCrypt.hashpw(oldPassword, BCrypt.gensalt());

        // Создаём тестового пользователя с заданным id и старым хешем пароля
        final User user = new User(
                userId,
                UserRole.USER,
                "Тестовый Пользователь",
                new Email("test@mail.com"),
                oldPasswordHash,
                LocalDateTime.now()
        );

        // Мокаем методы репозитория для поиска и сохранения пользователя
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // Формируем команду смены пароля
        final ChangePasswordCommand command = new ChangePasswordCommand(userId, oldPassword, newPassword);
        userService.changePassword(command);

        // Захватываем опубликованное событие смены пароля
        final ArgumentCaptor<PasswordChangedEvent> eventCaptor = ArgumentCaptor.forClass(PasswordChangedEvent.class);
        verify(passwordChangeEventPublisher, times(1)).publish(eventCaptor.capture());
        final PasswordChangedEvent event = eventCaptor.getValue();

        // Проверяем, что старый хеш совпадает с исходным значением
        assertEquals(oldPasswordHash, event.getOldPassword(),
                "Старый хеш должен быть равен исходному значению");

        // Новый хеш не должен совпадать со старым
        assertNotEquals(oldPasswordHash, event.getNewPassword(),
                "Новый хеш должен отличаться от старого");

        // Новый хеш соответствует новому паролю
        assertTrue(BCrypt.checkpw(newPassword, event.getNewPassword()),
                "Новый хеш должен совпадать с новым паролем");

        // Старый хеш НЕ соответствует новому паролю
        assertFalse(BCrypt.checkpw(newPassword, event.getOldPassword()),
                "Старый хеш не должен совпадать с новым паролем");
    }
}
