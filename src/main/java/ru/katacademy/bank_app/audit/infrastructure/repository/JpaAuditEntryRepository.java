package ru.katacademy.bank_app.audit.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.katacademy.bank_app.account.infrastructure.persistence.entity.AuditEntryEntity;

import java.util.List;

public interface JpaAuditEntryRepository extends JpaRepository<AuditEntryEntity, Long> {

    List<AuditEntryEntity> findByEventType(String eventType);

}

