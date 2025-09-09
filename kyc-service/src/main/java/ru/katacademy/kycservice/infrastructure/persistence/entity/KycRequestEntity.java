package ru.katacademy.kycservice.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
import ru.katacademy.bank_shared.enums.KycStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entity сущность для хранения данных заявки KYC в базе данных.
 * <p>
 * Поля:
 * - id: уникальный идентификатор записи, генерируется базой данных
 * - userId: идентификатор пользователя, которому принадлежит заявка
 * - status: текущий статус заявки (используется перечисление KycStatus)
 * - createdAt: дата и время создания записи (UTC, ISO-8601), автоматически проставляется при сохранении
 * - updatedAt: дата и время последнего обновления записи (UTC, ISO-8601), автоматически обновляется при каждом изменении сущности; на вставке
 *   также устанавливается за счёт DEFAULT now() в БД.
 * <p>
 * Автор: Кирюшин А.А.
 * Дата: 2025-08-05
 */
@Entity
@Table(name = "kyc_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class KycRequestEntity {
    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private KycStatus status = KycStatus.PENDING;

    @Column(name = "created_at", columnDefinition = "timestamp with time zone default now()")
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "timestamp with time zone default now()")
    private OffsetDateTime updatedAt;
}


