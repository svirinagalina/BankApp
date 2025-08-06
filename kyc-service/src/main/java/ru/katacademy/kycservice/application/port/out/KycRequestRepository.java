package ru.katacademy.kycservice.application.port.out;

import ru.katacademy.kycservice.domain.entity.KycRequest;

public interface KycRequestRepository {
    KycRequest save(KycRequest request);
}
