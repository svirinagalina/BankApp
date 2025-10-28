package ru.katacademy.bank_app.settingsservice.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.katacademy.bank_app.settingsservice.infrastructure.persistence.entity.UserSettingsEntity;

/**
 * Spring Data JPA‑репозиторий для работы с {@link UserSettingsEntity}.
 */
public interface UserSettingsJpaRepository extends JpaRepository<UserSettingsEntity, Long> {
}
