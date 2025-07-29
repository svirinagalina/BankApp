package ru.katacademy.bank_app.accountservice.infrastructure.repository;

import org.springframework.stereotype.Repository;
import ru.katacademy.bank_app.accountservice.application.port.out.UserRepository;
import ru.katacademy.bank_app.accountservice.domain.entity.User;
import ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity.UserEntity;
import ru.katacademy.bank_app.accountservice.infrastructure.persistence.mapper.UserMapper;
import ru.katacademy.bank_shared.valueobject.Email;

import java.util.Optional;

import static ru.katacademy.bank_app.accountservice.infrastructure.persistence.mapper.UserMapper.toDomain;
import static ru.katacademy.bank_app.accountservice.infrastructure.persistence.mapper.UserMapper.toEntity;

/**
 * Реализация порта {@link UserRepository}.
 * Делегирует операции JPA‑репозиторию и маппит сущности ↔ домен.
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;

    public UserRepositoryImpl(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpaRepository.findByEmail(email)
                .map(UserMapper::toDomain);
    }

    @Override
    public User save(User user) {
        final UserEntity entity = toEntity(user);
        final UserEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }
}
