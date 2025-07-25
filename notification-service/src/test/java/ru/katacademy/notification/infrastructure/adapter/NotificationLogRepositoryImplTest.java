package ru.katacademy.notification.infrastructure.adapter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import ru.katacademy.notification.application.port.out.NotificationLogRepository;
import ru.katacademy.notification.domain.model.NotificationLogEntry;
import ru.katacademy.notification.infrastructure.mapper.NotificationLogMapper;
import ru.katacademy.notification.infrastructure.persistence.repository.NotificationLogJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest(
        excludeAutoConfiguration = LiquibaseAutoConfiguration.class
)
@Import({ NotificationLogRepositoryImpl.class, NotificationLogMapper.class })
class NotificationLogRepositoryImplTest {

    @Autowired
    private NotificationLogJpaRepository jpaRepo;

    @Autowired
    private NotificationLogRepository repo;

    @Test
    void saveAndFindAll() {
        LocalDateTime t1 = LocalDateTime.parse("2025-07-24T08:00:00");
        LocalDateTime t2 = LocalDateTime.parse("2025-07-24T09:00:00");

        NotificationLogEntry e1 = new NotificationLogEntry(null, "Msg1", t1);
        NotificationLogEntry e2 = new NotificationLogEntry(null, "Msg2", t2);

        NotificationLogEntry saved1 = repo.save(e1);
        NotificationLogEntry saved2 = repo.save(e2);

        assertThat(saved1.getId()).isNotNull();
        assertThat(saved2.getId()).isNotNull();

        List<NotificationLogEntry> all = repo.findAll();
        assertThat(all).hasSize(2)
                .extracting(NotificationLogEntry::getMessage)
                .containsExactlyInAnyOrder("Msg1", "Msg2");
    }
}
