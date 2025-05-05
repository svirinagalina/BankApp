package ru.katacademy.bank_app.audit.infrastructure.repository;

import org.springframework.stereotype.Repository;
import ru.katacademy.bank_app.audit.domain.entity.AuditEntry;
import ru.katacademy.bank_app.audit.domain.repository.AuditRepository;
import ru.katacademy.bank_app.audit.infrastructure.persistence.entity.AuditEntryEntity;
import ru.katacademy.bank_app.audit.infrastructure.persistence.mapper.AuditMapper;

import java.util.List;

/**
 * Реализация репозитория для работы с записями аудита.
 * <p>
 * Класс предоставляет реализацию методов {@link AuditRepository} для сохранения записей аудита
 * в базу данных с использованием Spring Data JPA.
 *
 * @see org.springframework.stereotype.Repository
 * @see AuditRepository
 */
@Repository
public class AuditRepositoryImpl implements AuditRepository {
    private final JpaAuditEntryRepository jpaRepository;

    public AuditRepositoryImpl(JpaAuditEntryRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    /**
     * Сохраняет запись аудита в базу данных.
     * <p>
     * Делегирует операцию сохранения стандартному JPA-репозиторию.
     *
     * @param auditEntry сохраняемая запись аудита.
     */
    @Override
    public void save(AuditEntry auditEntry) {

        jpaRepository.save(AuditMapper.toEntity(auditEntry));
    }

    @Override
    public List<AuditEntry> getAllAudits() {
        final List<AuditEntryEntity> entities = jpaRepository.findAll();
        return entities.stream().map(AuditMapper::toDomain).toList();
    }

    @Override
    public List<AuditEntry> getAllAuditsByType(String eventType) {
        final List<AuditEntryEntity> entities = jpaRepository.findByEventType(eventType);
        return entities.stream().map(AuditMapper::toDomain).toList();
    }
}
