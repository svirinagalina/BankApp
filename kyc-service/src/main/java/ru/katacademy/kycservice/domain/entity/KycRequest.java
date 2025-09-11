package ru.katacademy.kycservice.domain.entity;


import lombok.*;
import ru.katacademy.bank_shared.enums.KycStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Класс представляет domain модель KycRequest
 * <p>
 * Поля:
 * - id: уникальный идентификатор заявки
 * - userId: идентификатор пользователя, который подал заявку
 * - status: текущий статус заявки (например, PENDING - ожидает обработки)
 * - createdAt: дата и время подачи заявки
 * - updatedAt: момент последнего обновления заявки
 * <p>
 * Автор: Кирюшин А.А.
 * Дата: 2025-08-05
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class KycRequest {
    private UUID id;

    private Long userId;

    private KycStatus status = KycStatus.PENDING;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

}
