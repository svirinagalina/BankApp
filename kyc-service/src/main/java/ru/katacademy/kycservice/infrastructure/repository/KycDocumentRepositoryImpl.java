package ru.katacademy.kycservice.infrastructure.repository;

import org.springframework.stereotype.Repository;
import ru.katacademy.kycservice.application.port.out.KycDocumentRepository;
import ru.katacademy.kycservice.domain.entity.KycDocument;
import ru.katacademy.kycservice.exception.KycNotFoundException;
import ru.katacademy.kycservice.infrastructure.persistence.mapper.KycDocumentMapper;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Реализация интерфейса репозитория для управления документами KYC с использованием JPA
 * <p>
 * Поля:
 * - jpaRepository: интерфейс JPA для взаимодействия с базой данных
 * - requestJpaRepository: JPA-репозиторий заявок
 * - mapper: маппер доменной модели
 * <p>
 * Методы:
 * - save: находит связанную заявку по kycRequestId, преобразует доменную модель в сущность и сохраняет документ в базе данных
 * <p>
 * Автор: Белявский Г.А.
 * Дата: 2025-09-05
 */
@Repository
public class KycDocumentRepositoryImpl implements KycDocumentRepository {
    private final KycDocumentJpaRepository jpaRepository;
    private final KycRequestJpaRepository requestJpaRepository;
    private final KycDocumentMapper mapper;
    private final Map<String, KycDocument> store = new ConcurrentHashMap<>();

    public KycDocumentRepositoryImpl(KycDocumentJpaRepository jpaRepository, KycRequestJpaRepository requestJpaRepository, KycDocumentMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.requestJpaRepository = requestJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public void save(KycDocument doc) {
        var requestEntity = requestJpaRepository.findById(doc.getKycRequestId())
                .orElseThrow(() -> new KycNotFoundException(doc.getKycRequestId()));

        var entity = mapper.toEntity(doc, requestEntity);
        jpaRepository.saveAndFlush(entity);
    }

    @Override
    public Optional<KycDocument> findById(String documentId) {
        return Optional.ofNullable(store.get(documentId));
    }
}
