package ru.katacademy.bank_app.audit.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.katacademy.bank_app.audit.persistence.entity.AuditEntryEntity;

public interface JpaAuditEntryRepository extends JpaRepository<AuditEntryEntity, Long> {
}
