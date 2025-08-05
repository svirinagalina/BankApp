package ru.katacademy.kycservice.infrastructure.persistence.mapper;


import ru.katacademy.kycservice.domain.entity.KycRequest;
import ru.katacademy.kycservice.infrastructure.persistence.entity.KycRequestEntity;


public class KycRequestMapper {
    public static KycRequestEntity toEntity(KycRequest kycRequest) {
        return new KycRequestEntity(
                kycRequest.getId(),
                kycRequest.getUserId(),
                kycRequest.getDocumentType(),
                kycRequest.getFileKey(),
                kycRequest.getStatus(),
                kycRequest.getSubmittedAt()
        );
    }

    public static KycRequest toDomain(KycRequestEntity kycRequestEntity) {
        return new KycRequest(
                kycRequestEntity.getId(),
                kycRequestEntity.getUserId(),
                kycRequestEntity.getDocumentType(),
                kycRequestEntity.getFileKey(),
                kycRequestEntity.getStatus(),
                kycRequestEntity.getSubmittedAt()
        );
    }
}
