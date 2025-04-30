package ru.katacademy.bank_app.account.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.katacademy.bank_app.account.domain.entity.AuditEntry;
import ru.katacademy.bank_app.account.infrastructure.persistence.entity.AuditEntryEntity;

public interface JpaAuditEntryRepository extends JpaRepository<AuditEntryEntity, Long> {

}
