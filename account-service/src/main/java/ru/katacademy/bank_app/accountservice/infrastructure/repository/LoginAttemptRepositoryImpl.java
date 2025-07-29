package ru.katacademy.bank_app.accountservice.infrastructure.repository;

import org.springframework.stereotype.Component;
import ru.katacademy.bank_app.accountservice.application.port.out.LoginAttemptRepository;
import ru.katacademy.bank_app.accountservice.domain.entity.LoginAttemptEntry;
import ru.katacademy.bank_app.accountservice.infrastructure.persistence.mapper.LoginAttemptEntryMapper;

import java.util.Optional;

/**
 * Реализация порта {@link LoginAttemptRepository}.
 * Делегирует операции JPA‑репозиторию и маппит сущности ↔ домен.
 */
@Component
public class LoginAttemptRepositoryImpl implements LoginAttemptRepository {

    private final LoginAttemptEntryJpaRepository jpa;

    /**
     * @param jpa JPA‑репозиторий для entity LoginAttemptEntryEntity
     */
    public LoginAttemptRepositoryImpl(LoginAttemptEntryJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<LoginAttemptEntry> findById(Long id) {
        return jpa.findById(id).map(LoginAttemptEntryMapper::toDomain);
    }

    @Override
    public LoginAttemptEntry save(LoginAttemptEntry a) {
        final var entity = LoginAttemptEntryMapper.toEntity(a);
        final var saved = jpa.save(entity);
        return LoginAttemptEntryMapper.toDomain(saved);
    }
}
