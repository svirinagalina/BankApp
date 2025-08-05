package ru.katacademy.kycservice.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.katacademy.kycservice.domain.enumtype.KycStatus;

import java.time.LocalDateTime;

/**
 * Entity cущность для хранения данных заявки KYC в базе данных.
 * <p>
 * Поля:
 * - id: уникальный идентификатор записи, генерируется базой данных
 * - userId: идентификатор пользователя, которому принадлежит заявка
 * - documentType: тип загруженного документа
 * - fileKey: ключ или ссылка на файл с документом
 * - status: текущий статус заявки (используется перечисление KycStatus)
 * - submittedAt: дата и время создания записи, автоматически проставляется при сохранении
 * <p>
 * Автор: Кирюшин А.А.
 * Дата: 2025-08-05
 */
@Entity
@Table(name = "kyc_verifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class KycRequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "documentType")
    private String documentType;

    @Column(name = "fileKey")
    private String fileKey;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private KycStatus status = KycStatus.PENDING;

    @Column(name = "submittedAt")
    @CreationTimestamp
    private LocalDateTime submittedAt;
}


