package ru.katacademy.bank_app.audit.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.katacademy.bank_app.audit.domain.entity.AuditEntry;
import ru.katacademy.bank_app.audit.domain.repository.AuditRepository;
import ru.katacademy.bank_app.audit.persistence.entity.AuditEntryEntity;
import ru.katacademy.bank_app.audit.persistence.mapper.AuditEntryMapper;

/**
 * Реализация интерфейса {@link AuditRepository}, отвечающая за сохранение событий аудита
 * в базу данных с использованием JPA.
 * <p>
 * Поля:
 * - jpaRepository: используется для выполнения операций сохранения,
 * - mapper: используется для преобразования доменного объекта {@link AuditEntry}
 * в JPA-сущность {@link AuditEntryEntity}.
 *
 * Автор: Maxim4212
 * Дата: 2025-05-10
 */
@Repository
@RequiredArgsConstructor
public class AuditRepositoryImpl implements AuditRepository {

    private final JpaAuditEntryRepository jpaRepository;
    private final AuditEntryMapper mapper;

    /**
     * Сохраняет объект {@link AuditEntry} в базу данных.
     *
     * @param auditEntry объект события аудита, подлежащий сохранению
     */
    @Override
    public void save(AuditEntry auditEntry) {
        final AuditEntryEntity entity = mapper.toEntity(auditEntry);
        jpaRepository.save(entity);
    }
}
