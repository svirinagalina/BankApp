package ru.katacademy.kycservice.domain.entity;


import lombok.*;
import ru.katacademy.kycservice.domain.enumtype.KycStatus;

import java.time.LocalDateTime;

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
