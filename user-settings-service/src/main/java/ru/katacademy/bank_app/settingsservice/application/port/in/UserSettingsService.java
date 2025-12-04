package ru.katacademy.bank_app.settingsservice.application.port.in;

import ru.katacademy.bank_app.settingsservice.application.dto.UserSettingsDto;
import ru.katacademy.bank_app.settingsservice.application.command.UpdateSettingsCommand;

public interface UserSettingsService {
    UserSettingsDto get(Long userId);
    void createOrUpdate(UserSettingsDto dto);
    void update(Long userId, UpdateSettingsCommand command);
    void reset(Long userId);
}
