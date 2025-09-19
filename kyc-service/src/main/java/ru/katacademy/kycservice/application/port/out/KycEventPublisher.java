package ru.katacademy.kycservice.application.port.out;

import ru.katacademy.bank_shared.event.kyc.KycStatusChangedEvent;

public interface KycEventPublisher {
    void publish(KycStatusChangedEvent kycStatusChangedEvent);
}
