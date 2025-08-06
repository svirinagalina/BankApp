package ru.katacademy.kycservice.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.katacademy.kycservice.infrastructure.persistence.entity.KycRequestEntity;

public interface KycRequestJpaRepository extends JpaRepository<KycRequestEntity, Long> {
}
