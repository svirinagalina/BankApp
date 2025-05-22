package ru.katacademy.bank_app.settingsservice.adapter.in.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.katacademy.bank_app.settingsservice.application.dto.UserSettingsDto;
import ru.katacademy.bank_app.settingsservice.application.command.UpdateSettingsCommand;
import ru.katacademy.bank_app.settingsservice.application.port.in.UserSettingsService;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class UserSettingsController {

    public final UserSettingsService userSettingsService;

    @GetMapping("/{userId}")
    public UserSettingsDto getUserSettings(@PathVariable Long userId) {
        return userSettingsService.get(userId);
    }

    @PostMapping
    public void createUserSettings(@RequestBody @Valid UserSettingsDto dto) {
        userSettingsService.createOrUpdate(dto);
    }

    @PatchMapping("/{userId}")
    public void updateUserSettings(@PathVariable Long userId, @RequestBody UpdateSettingsCommand command) {
        userSettingsService.update(userId, command);
    }

    @DeleteMapping("/{userId}")
    public void resetUserSettings(@PathVariable Long userId) {
        userSettingsService.reset(userId);
    }
}
