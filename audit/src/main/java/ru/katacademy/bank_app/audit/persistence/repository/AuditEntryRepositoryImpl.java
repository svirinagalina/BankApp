package ru.katacademy.bank_app.audit.persistence.repository;

import org.springframework.stereotype.Repository;
import ru.katacademy.bank_app.audit.application.port.out.AuditEntryRepository;
import ru.katacademy.bank_app.audit.domain.entity.AuditEntry;
import ru.katacademy.bank_app.audit.persistence.entity.AuditEntryEntity;
import ru.katacademy.bank_app.audit.persistence.mapper.AuditEntryMapper;

import java.util.List;
import java.util.stream.Collectors;

import static ru.katacademy.bank_app.audit.persistence.mapper.AuditEntryMapper.*;

/**
 * Реализация порта {@link AuditEntryRepository}.
 * Делегирует операции JPA‑репозиторию и маппит сущности ↔ домен.
 */
@Repository
public class AuditEntryRepositoryImpl implements AuditEntryRepository {

    private final JpaAuditEntryRepository jpa;

    public AuditEntryRepositoryImpl(JpaAuditEntryRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public AuditEntry save(AuditEntry entry) {
        final AuditEntryEntity e = toEntity(entry);
        final AuditEntryEntity saved = jpa.save(e);
        return toDomain(saved);
    }

    @Override
    public List<AuditEntry> findAll() {
        return jpa.findAll()
                .stream()
                .map(AuditEntryMapper::toDomain)
                .collect(Collectors.toList());
    }
}
