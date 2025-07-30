package ru.katacademy.auth.infrastructure.repository;

import org.springframework.stereotype.Repository;
import ru.katacademy.auth.application.port.out.LoginAttemptRepository;
import ru.katacademy.auth.domain.entity.LoginAttempt;
import ru.katacademy.auth.infrastructure.persistence.mapper.LoginAttemptEntityMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.katacademy.auth.infrastructure.persistence.mapper.LoginAttemptEntityMapper.toDomain;
import static ru.katacademy.auth.infrastructure.persistence.mapper.LoginAttemptEntityMapper.toEntity;

/**
 * Реализация порта {@link LoginAttemptRepository} для работы с БД через JPA.
 * <p>Делегирует CRUD‑операции Spring Data репозиторию и мапит слои через {@link LoginAttemptEntityMapper}.</p>
 */
@Repository
public class LoginAttemptRepositoryImpl implements LoginAttemptRepository {

    private final LoginAttemptJpaRepository jpa;

    /**
     * @param jpa Spring Data репозиторий для {@link ru.katacademy.infrastructure.persistence.entity.LoginAttemptEntity}
     */
    public LoginAttemptRepositoryImpl(LoginAttemptJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<LoginAttempt> findByUserId(Long userId) {
        return jpa.findByUserId(userId).stream()
                .map(LoginAttemptEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<LoginAttempt> findByTimestamp(LocalDateTime start, LocalDateTime end) {
        return jpa.findByTimestampBetween(start, end).stream()
                .map(LoginAttemptEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<LoginAttempt> findBySuccess(boolean success) {
        return jpa.findBySuccess(success).stream()
                .map(LoginAttemptEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public LoginAttempt save(LoginAttempt attempt) {
        var entity = toEntity(attempt);
        var saved = jpa.save(entity);
        return toDomain(saved);
    }
}
