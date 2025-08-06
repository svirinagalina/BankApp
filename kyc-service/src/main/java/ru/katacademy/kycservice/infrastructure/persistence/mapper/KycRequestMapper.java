package ru.katacademy.kycservice.infrastructure.persistence.mapper;


import ru.katacademy.kycservice.domain.entity.KycRequest;
import ru.katacademy.kycservice.infrastructure.persistence.entity.KycRequestEntity;

/**
 * Класс для преобразования между доменной моделью KycRequest и сущностью базы данных KycRequestEntity.
 * <p>
 * Методы:
 * - toEntity(KycRequest): преобразует объект доменной модели в объект сущности для хранения в базе данных
 * - toDomain(KycRequestEntity): преобразует объект сущности из базы данных в объект доменной модели
 * <p>
 * Автор: Кирюшин А.А.
 * Дата: 2025-08-05
 */
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
