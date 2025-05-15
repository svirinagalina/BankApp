package ru.katacademy.bank_app.settingsservice.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.katacademy.bank_app.settingsservice.application.dto.UserSettingsDto;
import ru.katacademy.bank_app.settingsservice.application.command.UpdateSettingsCommand;
import ru.katacademy.bank_app.settingsservice.application.mapper.UserSettingsMapper;
import ru.katacademy.bank_app.settingsservice.application.port.in.UserSettingsService;
import ru.katacademy.bank_app.settingsservice.application.port.out.SettingsChangedEventPublisher;
import ru.katacademy.bank_app.settingsservice.domain.entity.UserSettings;
import ru.katacademy.bank_app.settingsservice.application.port.out.UserSettingsRepository;
import ru.katacademy.bank_shared.event.SettingsChangedEvent;
import ru.katacademy.bank_shared.exception.AccountNotFoundException;

@Service
@RequiredArgsConstructor
public class UserRegisterServiceImpl implements UserSettingsService {

    private final UserSettingsRepository repo;
    private final SettingsChangedEventPublisher eventPublisher;

    @Override
    public UserSettingsDto get(Long userId) {
        return repo.findById(userId)
                .map(UserSettingsMapper::toDto)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
    }

    @Override
    public void createOrUpdate(UserSettingsDto dto) {
        repo.save(UserSettingsMapper.fromDto(dto));
    }

    @Override
    public void update(Long userId, UpdateSettingsCommand command) {
        final UserSettings settings = repo.findById(userId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        settings.setNotificationEnabled(command.getNotificationEnabled());
        settings.setDarkModeEnabled(command.getDarkModeEnabled());
        settings.setLanguage(String.valueOf(command.getLanguage()));

        repo.save(settings);

        final SettingsChangedEvent event = new SettingsChangedEvent(
                userId,
                command.getNotificationEnabled(),
                command.getLanguage(),
                command.getDarkModeEnabled());

        eventPublisher.publish(event);
    }

    @Override
    public void reset(Long userId) {
        repo.deleteById(userId);
    }
}
