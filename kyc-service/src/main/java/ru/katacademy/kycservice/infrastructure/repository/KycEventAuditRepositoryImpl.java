package ru.katacademy.kycservice.infrastructure.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.katacademy.bank_shared.enums.KycStatus;
import ru.katacademy.kycservice.application.port.out.KycEventAuditRepository;
import ru.katacademy.kycservice.exception.KycNotFoundException;
import ru.katacademy.kycservice.infrastructure.persistence.entity.KycEventEntity;

import java.util.UUID;

/**
 * Репозиторий аудита KYC-событий.
 * <p>
 * Сохраняет в БД запись о смене статуса KYC, связывая её с конкретной заявкой
 * Дата: 17.09.2025
 * Автор: Белявский Г.А.
 */
@Repository
public class KycEventAuditRepositoryImpl implements KycEventAuditRepository {

    private final KycEventJpaRepository events;
    private final KycRequestJpaRepository requests;

    public KycEventAuditRepositoryImpl(KycEventJpaRepository events,
                                       KycRequestJpaRepository requests) {
        this.events = events;
        this.requests = requests;
    }

    @Transactional
    @Override
    public void save(UUID kycRequestId, KycStatus status) {
        var requestEntity = requests.findById(kycRequestId)
                .orElseThrow(() -> new KycNotFoundException(kycRequestId));

        var eventEntity = new KycEventEntity();
        eventEntity.setKycRequest(requestEntity);
        eventEntity.setType(status.name());
        events.save(eventEntity);
    }
}