package ru.katacademy.kycservice.application.service;

import org.springframework.stereotype.Service;
import ru.katacademy.bank_shared.enums.KycStatus;
import ru.katacademy.bank_shared.event.kyc.KycStatusChangedEvent;
import ru.katacademy.kycservice.application.port.out.KycDocumentRepository;
import ru.katacademy.kycservice.application.port.out.KycRequestRepository;
import ru.katacademy.kycservice.domain.entity.KycDocument;
import ru.katacademy.kycservice.domain.entity.KycRequest;
import ru.katacademy.kycservice.application.port.out.KycEventPublisher;

import java.time.Instant;

@Service
public class KycService {

    private final KycEventPublisher eventPublisher;
    private final KycDocumentRepository kycDocumentRepository;
    private final KycRequestRepository kycRequestRepository;

    public KycService(KycEventPublisher eventPublisher,
                      KycDocumentRepository kycDocumentRepository,
                      KycRequestRepository kycRequestRepository) {
        this.eventPublisher = eventPublisher;
        this.kycDocumentRepository = kycDocumentRepository;
        this.kycRequestRepository = kycRequestRepository;
    }

    /**
     * Завершает процесс KYC по документу.
     * - Валидирует документ.
     * - Обновляет статус заявки.
     * - Отправляет событие в Kafka.
     */
    public void completeKyc(String documentId) {
        // 1. Получаем документ
        KycDocument document = kycDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));

        boolean isValid = validateDocument(document);

        // 2. Получаем заявку по userId из документа
        KycRequest kycRequest = kycRequestRepository.findById(document.getKycRequestId())
                .orElseThrow(() -> new RuntimeException("KYC request not found for user: " + document.getKycRequestId()));

        // 3. Обновляем статус заявки
        kycRequest.setStatus(isValid ? KycStatus.APPROVED : KycStatus.REJECTED);
        kycRequestRepository.save(kycRequest);

        // 4. Отправляем событие в Kafka
        eventPublisher.publish(
                new KycStatusChangedEvent(
                        kycRequest.getUserId().toString(),
                        kycRequest.getStatus(),
                        Instant.now(),
                        "kyc-service"
                )
        );
    }

    /**
     * Проверяет документ KYC на валидность.
     */
    private boolean validateDocument(KycDocument document) {
        return "passport".equalsIgnoreCase(document.getType())
                && document.getFileKey() != null
                && !document.getFileKey().isBlank();
    }
}
