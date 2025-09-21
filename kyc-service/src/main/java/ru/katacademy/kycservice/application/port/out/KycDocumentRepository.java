package ru.katacademy.kycservice.application.port.out;

import ru.katacademy.kycservice.domain.entity.KycDocument;

import java.util.Optional;

public interface KycDocumentRepository {
    void save(KycDocument doc);

    Optional<KycDocument> findById(String documentId);
}
