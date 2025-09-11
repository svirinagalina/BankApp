package ru.katacademy.auth.bank_app.authstatisticsservice.infrastructure.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import ru.katacademy.auth.domain.entity.LoginAttempt;
import ru.katacademy.auth.infrastructure.mapper.LoginAttemptMapper;
import ru.katacademy.auth.infrastructure.repository.LoginAttemptAuthRepositoryImpl;
import ru.katacademy.auth.infrastructure.repository.LoginAttemptJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest(excludeAutoConfiguration = LiquibaseAutoConfiguration.class,
        properties = { "spring.cloud.config.enabled=false" })
class LoginAttemptAuthRepositoryImplTest {

    @Autowired
    private LoginAttemptJpaRepository jpaRepo;

    private final LoginAttemptMapper mapper = new LoginAttemptMapper();

    @Test
    void saveAndFindByUserId() {
        LoginAttemptAuthRepositoryImpl repo = new LoginAttemptAuthRepositoryImpl(jpaRepo, mapper);

        LocalDateTime ts1 = LocalDateTime.parse("2025-07-24T09:00:00");
        LoginAttempt a1 = new LoginAttempt(null, 100L, ts1, true, "10.0.0.1", "Agent1");
        LoginAttempt saved = repo.save(a1);
        assertThat(saved.getId()).isNotNull();

        List<LoginAttempt> byUser = repo.findByUserId(100L);
        assertThat(byUser).hasSize(1)
                .first()
                .satisfies(la -> {
                    assertThat(la.getUserId()).isEqualTo(100L);
                    assertThat(la.isSuccess()).isTrue();
                });
    }

    @Test
    void findBySuccessAndTimestamp() {
        LoginAttemptAuthRepositoryImpl repo = new LoginAttemptAuthRepositoryImpl(jpaRepo, mapper);

        LocalDateTime now = LocalDateTime.now();
        LoginAttempt s1 = repo.save(new LoginAttempt(null, 200L, now.minusHours(1), true, "1.1.1.1", "A1"));
        LoginAttempt f1 = repo.save(new LoginAttempt(null, 200L, now.minusMinutes(30), false, "2.2.2.2", "A2"));

        List<LoginAttempt> successes = repo.findBySuccess(true);
        assertThat(successes).extracting(LoginAttempt::isSuccess)
                .allMatch(b -> b, "должны быть только успешные");

        List<LoginAttempt> inRange = repo.findByTimestamp(now.minusHours(2), now);
        assertThat(inRange).extracting(LoginAttempt::getTimestamp)
                .allMatch(ts -> !ts.isBefore(now.minusHours(2)) && !ts.isAfter(now));
    }
}
