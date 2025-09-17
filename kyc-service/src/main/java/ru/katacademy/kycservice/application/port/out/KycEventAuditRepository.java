package ru.katacademy.kycservice.application.port.out;

import ru.katacademy.bank_shared.enums.KycStatus;

import java.util.UUID;

public interface KycEventAuditRepository {
    void save(UUID kycRequestId, KycStatus status);
}
