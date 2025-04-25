package ru.katacademy.bank_app.account.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.katacademy.bank_app.account.domain.entity.AuditEntry;

public interface JpaAuditEntryRepository extends JpaRepository<AuditEntry, Long> {

}
