package ru.katacademy.kycservice.domain.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Класс представляет domain-модель документа KYC, привязанного к заявке
 * <p>
 * Поля:
 * - id: уникальный идентификатор документа
 * - kycRequestId: идентификатор связанной заявки KYC
 * - type: тип документа
 * - fileKey: ключ к файлу в хранилище
 * - uploadedAt: момент загрузки документа
 * <p>
 * Автор: Белявский Г.А.
 * Дата: 2025-09-05
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class KycDocument {
    private UUID id;
    private UUID kycRequestId;
    private String type;
    private String fileKey;
    private OffsetDateTime uploadedAt;
}
