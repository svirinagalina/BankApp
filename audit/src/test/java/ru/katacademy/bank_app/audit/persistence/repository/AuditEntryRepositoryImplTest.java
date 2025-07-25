package ru.katacademy.bank_app.audit.persistence.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.katacademy.bank_app.audit.application.port.out.AuditEntryRepository;
import ru.katacademy.bank_app.audit.domain.entity.AuditEntry;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class AuditEntryRepositoryImplTest {

    @Autowired
    private JpaAuditEntryRepository jpaRepo;

    @Test
    void saveAndFindAll() {
        final AuditEntryRepository repo = new AuditEntryRepositoryImpl(jpaRepo);
        final AuditEntry e1 = new AuditEntry("T1", "m1", "u1");
        final AuditEntry e2 = new AuditEntry("T2", "m2", "u2");

        repo.save(e1);
        repo.save(e2);

        final List<AuditEntry> all = repo.findAll();
        assertThat(all).hasSize(2)
                .containsExactlyInAnyOrder(e1, e2);
    }
}
