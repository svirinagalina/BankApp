package ru.katacademy.kycservice.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.katacademy.kycservice.infrastructure.persistence.entity.KycRequestEntity;

import java.util.Optional;
import java.util.UUID;

public interface KycRequestJpaRepository extends JpaRepository<KycRequestEntity, UUID> {
    Optional<KycRequestEntity> findByUserId(Long userId);

    UUID id(UUID id);
}
