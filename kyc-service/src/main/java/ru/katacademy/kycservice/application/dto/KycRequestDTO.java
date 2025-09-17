package ru.katacademy.kycservice.application.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import ru.katacademy.bank_shared.enums.KycStatus;

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
@Schema(description = "DTO заявки KYC с текущим статусом и временем последнего обновления")
public record KycRequestDTO(

        @Schema(description = "Уникальный идентификатор заявки KYC", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(description = "Текущий статус заявки KYC", example = "PENDING")
        KycStatus status,

        @Schema(description = "Время последнего обновления заявки (UTC)", example = "2025-09-16T22:30:00Z")
        OffsetDateTime updatedAt
) {
}
