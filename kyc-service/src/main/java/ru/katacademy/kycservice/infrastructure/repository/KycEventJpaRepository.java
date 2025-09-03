package ru.katacademy.kycservice.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.katacademy.kycservice.infrastructure.persistence.entity.KycEventEntity;

import java.util.UUID;

public interface KycEventJpaRepository extends JpaRepository<KycEventEntity, UUID> {

}
