package ru.katacademy.bank_app.settingsservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.katacademy.bank_app.settingsservice.DTO.UserSettingsDto;
import ru.katacademy.bank_app.settingsservice.comand.UpdateSettingsCommand;
import ru.katacademy.bank_app.settingsservice.model.UserSettings;
import ru.katacademy.bank_app.settingsservice.repository.UserSettingsRepository;

@Service
@RequiredArgsConstructor
public class UserRegisterServiceImpl implements UserSettingsService{

    private final UserSettingsRepository userSettingsRepository;

    @Override
    public UserSettings get(Long userId) {

    }

    @Override
    public void createOrUpdate(UserSettingsDto dto) {

    }

    @Override
    public void update(Long userId, UpdateSettingsCommand command) {

    }

    @Override
    public void reset(Long userId) {

    }
}
