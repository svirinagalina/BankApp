package ru.katacademy.kycservice.infrastructure.repository;

import org.springframework.stereotype.Repository;
import ru.katacademy.kycservice.application.port.out.KycRequestRepository;
import ru.katacademy.kycservice.domain.entity.KycRequest;
import ru.katacademy.kycservice.infrastructure.persistence.entity.KycRequestEntity;
import ru.katacademy.kycservice.infrastructure.persistence.mapper.KycRequestMapper;


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
