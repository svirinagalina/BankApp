package ru.katacademy.bank_app.accountservice.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity.AccountEntity;

import java.util.Optional;

/**
 * JPA‑репозиторий для {@link AccountEntity}.
 */
public interface AccountJpaRepository extends JpaRepository<AccountEntity, Long> {
    long countByUserId(Long userId);
    Optional<AccountEntity> findById(Long id);
}
