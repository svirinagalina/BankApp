package ru.katacademy.bank_app.settingsservice.domain.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для {@link UserSettings} - сущности, представляющей пользовательские настройки.
 * Проверяет корректность работы:
 * - Тестирует создание объекта через Builder.
 * - Управления временными метками.
 * - Геттеров/сеттеров основных полей
 */
class UserSettingsTest {

    /**
     * Тестирует создание объекта через Builder.
     * Проверяет корректность инициализации всех полей.
     */
    @Test
    void shouldCreateUserSettingsWithBuilder() {
        final UserSettings settings = UserSettings.builder()
                .userId(1L)
                .notificationEnabled(true)
                .language("en")
                .darkModeEnabled(false)
                .build();

        assertEquals(1L, settings.getUserId());
        assertTrue(settings.isNotificationEnabled());
        assertEquals("en", settings.getLanguage());
        assertFalse(settings.isDarkModeEnabled());
    }

    /**
     * Тестирует автоматическую установку временных меток при создании.
     * Проверяет что:
     * 1. Поля created_at и updated_at инициализируются
     * 2. Имеют одинаковые значения при создании
     */
    @Test
    void shouldSetTimestampsOnCreate() {
        final UserSettings settings = new UserSettings();
        settings.onCreate();

        assertNotNull(settings.getCreatedAt());
        assertNotNull(settings.getUpdatedAt());
        assertEquals(
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                settings.getCreatedAt().truncatedTo(ChronoUnit.SECONDS)
        );
    }

    /**
     * Тестирует обновление временной метки updated_at.
     * Проверяет что:
     * 1. Метка обновляется при вызове onUpdate()
     * 2. Новое значение больше предыдущего
     */
    @Test
    void shouldUpdateTimestampOnUpdate() {
        final UserSettings settings = new UserSettings();
        settings.onCreate();
        final LocalDateTime initialUpdateTime = settings.getUpdatedAt();

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        settings.onUpdate();

        assertNotNull(settings.getUpdatedAt());
        assertTrue(settings.getUpdatedAt().isAfter(initialUpdateTime));
    }

    /**
     * Тестирует базовые геттеры и сеттеры.
     * Проверяет корректность установки и получения значений всех полей.
     */
    @Test
    void shouldHaveWorkingGettersAndSetters() {
        final UserSettings settings = new UserSettings();

        settings.setUserId(2L);
        settings.setNotificationEnabled(false);
        settings.setLanguage("ru");
        settings.setDarkModeEnabled(true);

        assertEquals(2L, settings.getUserId());
        assertFalse(settings.isNotificationEnabled());
        assertEquals("ru", settings.getLanguage());
        assertTrue(settings.isDarkModeEnabled());
    }
}