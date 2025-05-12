package ru.katacademy.bank_app.settingsservice.controller;

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
import ru.katacademy.bank_app.settingsservice.DTO.UserSettingsDto;
import ru.katacademy.bank_app.settingsservice.comand.UpdateSettingsCommand;
import ru.katacademy.bank_app.settingsservice.model.UserSettings;
import ru.katacademy.bank_app.settingsservice.service.UserSettingsService;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class UserSettingsController {

    public final UserSettingsService userSettingsService;

    @GetMapping("/{userId}")
    public UserSettings getUserSettings(@PathVariable Long userId) {
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

    @DeleteMapping
    public void resetUserSettings(@PathVariable Long userId) {
        userSettingsService.reset(userId);
    }
}
