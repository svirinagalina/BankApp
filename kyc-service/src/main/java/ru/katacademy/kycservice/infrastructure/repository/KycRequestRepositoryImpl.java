package ru.katacademy.kycservice.infrastructure.repository;

import org.springframework.stereotype.Repository;
import ru.katacademy.kycservice.application.port.out.KycRequestRepository;
import ru.katacademy.kycservice.domain.entity.KycRequest;
import ru.katacademy.kycservice.infrastructure.persistence.entity.KycRequestEntity;
import ru.katacademy.kycservice.infrastructure.persistence.mapper.KycRequestMapper;

/**
 * Реализация интерфейса репозитория для управления заявками KYC с использованием JPA.
 * <p>
 * Поля:
 * - jpaRepository: интерфейс JPA для взаимодействия с базой данных
 * <p>
 * Методы:
 * - save(KycRequest): сохраняет заявку KYC в базе данных и возвращает сохранённый объект с обновлёнными данными
 * <p>
 * Автор: Кирюшин А.А.
 * Дата: 2025-08-05
 */
@Repository
public class KycRequestRepositoryImpl implements KycRequestRepository {
    private final KycRequestJpaRepository jpaRepository;

    public KycRequestRepositoryImpl(KycRequestJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }


    @Override
    public KycRequest save(KycRequest request) {
        final KycRequestEntity entity = KycRequestMapper.toEntity(request);
        final KycRequestEntity savedEntity = jpaRepository.save(entity);
        return KycRequestMapper.toDomain(savedEntity);
    }
}
