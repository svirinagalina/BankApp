package ru.katacademy.bank_app.settingsservice.application.port.out;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.katacademy.bank_app.settingsservice.domain.entity.UserSettings;

public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
}
