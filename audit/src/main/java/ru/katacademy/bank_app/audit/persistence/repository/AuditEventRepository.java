package ru.katacademy.bank_app.audit.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.katacademy.bank_app.audit.persistence.entity.AuditEvent;


public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {
}
