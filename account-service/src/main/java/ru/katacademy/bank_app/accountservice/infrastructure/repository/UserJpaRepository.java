package ru.katacademy.bank_app.accountservice.infrastructure.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity.UserEntity;
import ru.katacademy.bank_shared.valueobject.Email;

import java.util.Optional;

/**
 * JPA‑репозиторий для {@link UserEntity}.
 * Поддерживает поиск по email.
 */
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(Email email);
}
