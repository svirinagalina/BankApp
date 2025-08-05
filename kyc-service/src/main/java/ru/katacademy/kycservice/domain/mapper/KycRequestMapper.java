package ru.katacademy.kycservice.domain.mapper;

import org.springframework.stereotype.Component;
import ru.katacademy.kycservice.application.dto.KycRequestDTO;
import ru.katacademy.kycservice.domain.entity.KycRequest;

/**
 * Компонент для преобразования сущности KycRequest в DTO-объект KycRequestDTO.
 * <p>
 * Методы:
 * - toDTO(KycRequest kycRequest): преобразует доменную сущность KycRequest в DTO, содержащий id и статус заявки
 * <p>
 * Автор: Кирюшин А.А.
 * Дата: 2025-08-05
 */
@Component
public class KycRequestMapper {
    public KycRequestDTO toDTO(KycRequest kycRequest) {
        return new KycRequestDTO(
                kycRequest.getId(),
                kycRequest.getStatus());
    }
}
