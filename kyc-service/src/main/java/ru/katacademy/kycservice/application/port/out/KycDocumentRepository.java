package ru.katacademy.kycservice.application.port.out;

import ru.katacademy.kycservice.domain.entity.KycDocument;

public interface KycDocumentRepository {
    void save(KycDocument doc);
}
