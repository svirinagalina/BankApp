package ru.katacademy.bank_app.settingsservice.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.katacademy.bank_app.settingsservice.application.port.out.UserSettingsRepository;
import ru.katacademy.bank_app.settingsservice.domain.entity.UserSettings;
import ru.katacademy.bank_app.settingsservice.infrastructure.mapper.UserSettingsEntityMapper;
import ru.katacademy.bank_app.settingsservice.infrastructure.persistence.repository.UserSettingsJpaRepository;

import java.util.Optional;

/**
 * Реализация {@link UserSettingsRepository} через Spring Data JPA.
 * <p>Делегирует операции CRUD JPA‑репозиторию {@link UserSettingsJpaRepository}
 * и преобразует сущности через {@link UserSettingsEntityMapper}.</p>
 */
@Repository
@RequiredArgsConstructor
public class UserSettingsRepositoryImpl implements UserSettingsRepository {
    private final UserSettingsJpaRepository jpa;

    @Override
    public Optional<UserSettings> findById(Long userId) {
        return jpa.findById(userId)
                .map(UserSettingsEntityMapper::toDomain);
    }

    @Override
    public UserSettings save(UserSettings settings) {
        final var entity = UserSettingsEntityMapper.toEntity(settings);
        final var saved = jpa.save(entity);
        return UserSettingsEntityMapper.toDomain(saved);
    }

    @Override
    public void deleteById(Long userId) {
        jpa.deleteById(userId);
    }
}
