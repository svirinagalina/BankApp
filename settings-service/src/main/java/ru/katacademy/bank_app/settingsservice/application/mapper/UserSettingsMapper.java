package ru.katacademy.bank_app.settingsservice.application.mapper;

import ru.katacademy.bank_app.settingsservice.application.dto.UserSettingsDto;
import ru.katacademy.bank_app.settingsservice.domain.entity.UserSettings;
import ru.katacademy.bank_shared.valueobject.Languages;

public class UserSettingsMapper {

    public static UserSettingsDto toDto(UserSettings userSettings) {
        return UserSettingsDto.builder()
                .userId(userSettings.getUserId())
                .notificationEnabled(userSettings.isNotificationEnabled())
                .language(Languages.valueOf(userSettings.getLanguage()))
                .darkModeEnabled(userSettings.isDarkModeEnabled())
                .build();
    }

    public static UserSettings fromDto(UserSettingsDto dto) {
        return UserSettings.builder()
                .userId(dto.getUserId())
                .notificationEnabled(dto.isNotificationEnabled())
                .language(String.valueOf(dto.getLanguage()))
                .darkModeEnabled(dto.isDarkModeEnabled())
                .build();
    }
}
