package ru.katacademy.kycservice.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Аудит-событие KYC (для будущего использования).
 * Таблица: kyc_event
 * <p>
 *     Поля:
 * - id: уникальный идентификатор события
 * - kycRequest: внешний ключ(FK) на заявку, к которой относится событие
 * - type: тип события
 * - createdAt: момент фиксации события (UTC). Проставляется автоматически при INSERT
 * <p/>
 * Автор: Белявский Г.А.
 * Дата: 2025-09-01
 */
@Entity
@Table(name = "kyc_event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class KycEventEntity {

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "kyc_request_id", nullable = false)
    private KycRequestEntity kycRequest;

    @Column(name = "type", nullable = false, length = 64)
    private String type;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false,
            columnDefinition = "timestamp with time zone default now()")
    private OffsetDateTime createdAt;
}
