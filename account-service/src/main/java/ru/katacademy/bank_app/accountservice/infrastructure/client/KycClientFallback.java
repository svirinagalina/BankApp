package ru.katacademy.bank_app.accountservice.infrastructure.client;

import org.springframework.stereotype.Component;
import ru.katacademy.bank_app.accountservice.adapters.web.request.kyc.UpdateKycStatusRequest;
import ru.katacademy.bank_shared.exception.KycServiceUnavailableException;

@Component
public class KycClientFallback implements KycClient {
    @Override
    public UpdateKycStatusRequest getKyc(Long userId) {
        throw new KycServiceUnavailableException("Verification service temporarilly unavailable");
    }

    @Override
    public void startKyc(Long userId) {
        throw new KycServiceUnavailableException("KYC service temporarily unavailable");
    }
}
