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
 * Entity-сущность для хранения загруженных документов, привязанных к KYC-заявке
 * Поля:
 * - id: уникальный идентификатор документа (UUID), генерируется БД;
 * - kycRequest: ссылка (FK) на заявку KYC, к которой относится документ;
 * - type: тип документа (например, "passport", "selfie");
 * - fileKey: ключ/путь объекта в хранилище MinIO (S3-совместимый);
 * - uploadedAt: дата и время загрузки (UTC, ISO-8601), выставляется автоматически при вставке.
 */
@Entity
@Table(name = "kyc_document")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class KycDocumentEntity {

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "kyc_request_id", nullable = false)
    private KycRequestEntity kycRequest;

    @Column(name = "type", nullable = false, length = 64)
    private String type;

    @Column(name = "file_key", nullable = false, length = 512)
    private String fileKey;

    @CreationTimestamp
    @Column(name = "uploaded_at", nullable = false,columnDefinition = "timestamp with time zone default now()")
    private OffsetDateTime uploadedAt;
}
