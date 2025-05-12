package ru.katacademy.bank_app.settingsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.katacademy.bank_app.settingsservice.model.UserSettings;

public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
}
