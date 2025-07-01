package ru.katacademy.bank_app.settingsservice.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.katacademy.bank_app.settingsservice.application.command.UpdateSettingsCommand;
import ru.katacademy.bank_app.settingsservice.application.dto.UserSettingsDto;
import ru.katacademy.bank_app.settingsservice.application.port.out.SettingsChangedEventPublisher;
import ru.katacademy.bank_app.settingsservice.application.port.out.UserSettingsRepository;
import ru.katacademy.bank_app.settingsservice.domain.entity.UserSettings;
import ru.katacademy.bank_shared.event.SettingsChangedEvent;
import ru.katacademy.bank_shared.exception.AccountNotFoundException;
import ru.katacademy.bank_shared.valueobject.Languages;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Тестовый класс для {@link UserRegisterServiceImpl} - сервиса работы с пользовательскими настройками.
 * Проверяет корректность работы основных операций: получение, создание/обновление, сброс настроек.
 */
@ExtendWith(MockitoExtension.class)
class UserRegisterServiceImplTest {

    @Mock
    private UserSettingsRepository repo;

    @Mock
    private SettingsChangedEventPublisher eventPublisher;

    @InjectMocks
    private UserRegisterServiceImpl service;

    private final Long testUserId = 1L;

    /**
     * Тест проверяет успешное получение настроек пользователя, когда они существуют в репозитории.
     * Ожидается, что метод вернет DTO с корректными значениями полей.
     */
    @Test
    void get_ShouldReturnSettings_WhenExists() {
        final UserSettings settings = UserSettings.builder()
                .userId(testUserId)
                .notificationEnabled(true)
                .language("EN")
                .darkModeEnabled(false)
                .build();

        when(repo.findById(testUserId)).thenReturn(Optional.of(settings));

        final UserSettingsDto result = service.get(testUserId);

        assertNotNull(result);
        assertEquals(testUserId, result.getUserId());
        assertTrue(result.isNotificationEnabled());
        assertFalse(result.isDarkModeEnabled());
        verify(repo).findById(testUserId);
    }

    /**
     * Тест проверяет обработку случая, когда настройки пользователя не найдены.
     * Ожидается выброс исключения AccountNotFoundException.
     */
    @Test
    void get_ShouldThrowException_WhenNotFound() {
        when(repo.findById(testUserId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> service.get(testUserId));
    }

    /**
     * Тест проверяет базовую функциональность создания/обновления настроек.
     * Ожидается, что метод save репозитория будет вызван ровно один раз.
     */
    @Test
    void createOrUpdate_ShouldSaveSettings() {
        final UserSettingsDto dto = new UserSettingsDto();
        service.createOrUpdate(dto);

        verify(repo).save(any(UserSettings.class));
    }

    /**
     * Тест проверяет обновление существующих настроек пользователя.
     * Проверяет:
     * 1) Корректность обновления полей настроек
     * 2) Сохранение обновленных настроек
     * 3) Публикацию события об изменении настроек
     */
    @Test
    void update_ShouldUpdateSettingsAndPublishEvent_WhenSettingsExist() {
        // Подготовка
        final UserSettings existingSettings = UserSettings.builder()
                .userId(testUserId)
                .notificationEnabled(false)
                .language("RU")
                .darkModeEnabled(false)
                .build();

        final UpdateSettingsCommand command = new UpdateSettingsCommand();
        command.setNotificationEnabled(true);
        command.setLanguage(Languages.EN);
        command.setDarkModeEnabled(true);

        when(repo.findById(testUserId)).thenReturn(Optional.of(existingSettings));

        service.update(testUserId, command);

        verify(repo).findById(testUserId);
        assertTrue(existingSettings.isNotificationEnabled());
        assertEquals("EN", existingSettings.getLanguage());
        assertTrue(existingSettings.isDarkModeEnabled());
        verify(repo).save(existingSettings);
        verify(eventPublisher).publish(any(SettingsChangedEvent.class));
    }

    /**
     * Тест проверяет обработку случая, когда настройки для обновления не найдены.
     * Ожидается:
     * 1) Выброс исключения AccountNotFoundException
     * 2) Отсутствие вызовов методов save и publish
     */
    @Test
    void update_ShouldThrowException_WhenSettingsNotFound() {
        final UpdateSettingsCommand command = new UpdateSettingsCommand();
        command.setNotificationEnabled(true);
        command.setLanguage(Languages.EN);
        command.setDarkModeEnabled(true);

        when(repo.findById(testUserId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> service.update(testUserId, command));
        verify(repo, never()).save(any());
        verify(eventPublisher, never()).publish(any());
    }

    /**
     * Тест проверяет функциональность сброса настроек пользователя.
     * Ожидается вызов метода deleteById репозитория с правильным userId.
     */
    @Test
    void reset_ShouldDeleteSettings() {
        service.reset(testUserId);
        verify(repo).deleteById(testUserId);
    }
}