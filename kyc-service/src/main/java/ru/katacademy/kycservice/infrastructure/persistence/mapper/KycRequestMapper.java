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
    private KycRequestMapper() {}
    public static KycRequestEntity toEntity(KycRequest kycRequest) {
        KycRequestEntity kycRequestEntity = new KycRequestEntity();
        kycRequestEntity.setId(kycRequest.getId());
        kycRequestEntity.setUserId(kycRequest.getUserId());
        kycRequestEntity.setStatus(kycRequest.getStatus());
        return kycRequestEntity;
    }

    public static KycRequest toDomain(KycRequestEntity kycRequestEntity) {
        KycRequest kycRequest = new KycRequest();
        kycRequest.setId(kycRequestEntity.getId());
        kycRequest.setUserId(kycRequestEntity.getUserId());
        kycRequest.setStatus(kycRequestEntity.getStatus());
        kycRequest.setCreatedAt(kycRequestEntity.getCreatedAt());
        kycRequest.setUpdatedAt(kycRequestEntity.getUpdatedAt());

        return kycRequest;
    }
}
