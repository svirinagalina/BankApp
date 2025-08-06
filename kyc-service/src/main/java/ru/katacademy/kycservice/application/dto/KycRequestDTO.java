package ru.katacademy.kycservice.application.dto;


import ru.katacademy.kycservice.domain.enumtype.KycStatus;

/**
 * DTO-объект для передачи информации о статусе заявки KYC.
 * <p>
 * Поля:
 * - id: идентификатор заявки KYC
 * - status: текущий статус заявки (enum KycStatus)
 * <p>
 * Методы:
 * - генерация стандартных методов record (геттеры, equals, hashCode, toString)
 * <p>
 * Автор: Кирюшин А.А.
 * Дата: 2025-08-05
 */
public record KycRequestDTO(
        Long id,
        KycStatus status
) {
}
