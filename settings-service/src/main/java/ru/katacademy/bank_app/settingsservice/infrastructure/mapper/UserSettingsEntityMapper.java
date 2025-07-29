package ru.katacademy.bank_app.settingsservice.infrastructure.mapper;

import ru.katacademy.bank_app.settingsservice.domain.entity.UserSettings;
import ru.katacademy.bank_app.settingsservice.infrastructure.persistence.entity.UserSettingsEntity;

/**
 * Утилитный класс для преобразования между доменной моделью {@link UserSettings}
 * и JPA‑сущностью {@link UserSettingsEntity}.
 */
public class UserSettingsEntityMapper {

    /**
     * Преобразует доменный объект {@link UserSettings} в JPA‑сущность {@link UserSettingsEntity}.
     *
     * @param d доменная модель с текущими настройками
     * @return новая JPA‑сущность для сохранения в базу
     */
    public static UserSettingsEntity toEntity(UserSettings d) {
        final var e = new UserSettingsEntity();
        e.setUserId(d.getUserId());
        e.setNotificationEnabled(d.isNotificationEnabled());
        e.setLanguage(d.getLanguage());
        e.setDarkModeEnabled(d.isDarkModeEnabled());
        return e;
    }

    /**
     * Преобразует JPA‑сущность {@link UserSettingsEntity} в доменную модель {@link UserSettings}.
     *
     * @param e сущность, полученная из базы
     * @return новая доменная модель с теми же данными
     */
    public static UserSettings toDomain(UserSettingsEntity e) {
        return UserSettings.builder()
                .userId(e.getUserId())
                .notificationEnabled(e.isNotificationEnabled())
                .language(e.getLanguage())
                .darkModeEnabled(e.isDarkModeEnabled())
                .build();
    }
}
