package ru.katacademy.kycservice.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import ru.katacademy.kycservice.domain.entity.KycDocument;
import ru.katacademy.kycservice.infrastructure.persistence.entity.KycDocumentEntity;
import ru.katacademy.kycservice.infrastructure.persistence.entity.KycRequestEntity;

/**
 * Класс для преобразования между доменной моделью KycDocument и сущностью базы данных KycDocumentEntity
 * <p>
 * Методы:
 * - toEntity: преобразует доменную модель в JPA-сущность с привязкой к заявке
 * - toDomain: преобразует JPA-сущность в доменную модель
 * <p>
 * Автор: Белявский Г.А.
 * Дата: 2025-09-05
 */
@Component
public class KycDocumentMapper {

    public KycDocumentEntity toEntity(KycDocument kycDocument, KycRequestEntity kycRequestEntity) {
        KycDocumentEntity kycDocumentEntity = new KycDocumentEntity();
        kycDocumentEntity.setId(kycDocument.getId());
        kycDocumentEntity.setKycRequest(kycRequestEntity);
        kycDocumentEntity.setType(kycDocument.getType());
        kycDocumentEntity.setFileKey(kycDocument.getFileKey());
        return kycDocumentEntity;
    }

    public KycDocument toDomain(KycDocumentEntity kycDocumentEntity) {
        KycDocument kycDocument = new KycDocument();
        kycDocument.setId(kycDocumentEntity.getId());
        kycDocument.setKycRequestId(kycDocumentEntity.getKycRequest().getId());
        kycDocument.setType(kycDocumentEntity.getType());
        kycDocument.setFileKey(kycDocumentEntity.getFileKey());
        kycDocument.setUploadedAt(kycDocumentEntity.getUploadedAt());
        return kycDocument;
    }
}
