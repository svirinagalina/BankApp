package ru.katacademy.bank_app.settingsservice.service;

import ru.katacademy.bank_app.settingsservice.DTO.UserSettingsDto;
import ru.katacademy.bank_app.settingsservice.comand.UpdateSettingsCommand;
import ru.katacademy.bank_app.settingsservice.model.UserSettings;

public interface UserSettingsService {
    UserSettings get(Long userId);
    void createOrUpdate(UserSettingsDto dto);
    void update(Long userId, UpdateSettingsCommand command);
    void reset(Long userId);
}
