package ru.katacademy.kycservice.domain.entity;


import lombok.*;
import ru.katacademy.kycservice.domain.enumtype.KycStatus;

import java.time.LocalDateTime;

/**
 * Класс представляет domain модель KycRequest
 * <p>
 * Поля:
 * - id: уникальный идентификатор заявки
 * - userId: идентификатор пользователя, который подал заявку
 * - documentType: тип документа, предоставленного пользователем
 * - fileKey: ключ или ссылка на файл с документом
 * - status: текущий статус заявки (например, PENDING - ожидает обработки)
 * - submittedAt: дата и время подачи заявки
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
    private Long id;

    private Long userId;

    private String documentType;

    private String fileKey;

    private KycStatus status = KycStatus.PENDING;

    private LocalDateTime submittedAt;

    public KycRequest(Long userId, String documentType, String fileKey, LocalDateTime submittedAt) {
        this.userId = userId;
        this.documentType = documentType;
        this.fileKey = fileKey;
        this.submittedAt = submittedAt;
    }
}
