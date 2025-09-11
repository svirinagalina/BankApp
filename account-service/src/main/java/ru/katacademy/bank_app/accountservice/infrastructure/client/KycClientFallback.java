package ru.katacademy.bank_app.accountservice.infrastructure.client;

import org.springframework.stereotype.Component;
import ru.katacademy.bank_app.accountservice.application.dto.KycRequestDTO;
import ru.katacademy.bank_shared.exception.KycServiceUnavailableException;

@Component
public class KycClientFallback implements KycClient {
    @Override
    public KycRequestDTO getKyc(Long userId) {
        throw new KycServiceUnavailableException("Verification service temporarilly unavailable");
    }
}
