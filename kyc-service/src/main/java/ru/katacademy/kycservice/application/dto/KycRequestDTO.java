package ru.katacademy.kycservice.application.dto;


import ru.katacademy.kycservice.domain.enumtype.KycStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO-объект для передачи информации о статусе заявки KYC.
 * <p>
 * Поля:
 * - id: идентификатор заявки KYC
 * - status: текущий статус заявки (enum KycStatus)
 * - updatedAt: момент последнего обновления заявки в формате ISO-8601 (UTC)
 *    Если заявка ещё не изменялась после создания, значение равно времени создания
 * <p>
 * Методы:
 * - генерация стандартных методов record (геттеры, equals, hashCode, toString)
 * <p>
 * Автор: Кирюшин А.А.
 * Дата: 2025-08-05
 */
public record KycRequestDTO(
        UUID id,
        KycStatus status,
        OffsetDateTime updatedAt
) {
}
