package ru.katacademy.bank_app.account.infrastructure.repository;

import org.springframework.stereotype.Repository;
import ru.katacademy.bank_app.account.domain.entity.AuditEntry;
import ru.katacademy.bank_app.account.domain.repository.AuditRepository;
import ru.katacademy.bank_app.account.infrastructure.persistence.mapper.AuditMapper;

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
}
