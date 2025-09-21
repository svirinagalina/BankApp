package ru.katacademy.kycservice.application.port.out;

import ru.katacademy.kycservice.domain.entity.KycRequest;

import java.util.Optional;
import java.util.UUID;

public interface KycRequestRepository {
    KycRequest save(KycRequest request);
    Optional<KycRequest> findByUserId(Long userId);
    Optional<KycRequest> findById(UUID userId);
}
