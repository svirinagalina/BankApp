package ru.katacademy.kycservice.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.katacademy.kycservice.infrastructure.persistence.entity.KycDocumentEntity;

import java.util.UUID;

public interface KycDocumentJpaRepository extends JpaRepository<KycDocumentEntity, UUID> {
}
