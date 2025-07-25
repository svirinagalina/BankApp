package ru.katacademy.bank_app.accountservice.infrastructure.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import ru.katacademy.bank_app.accountservice.application.port.out.LoginAttemptRepository;
import ru.katacademy.bank_app.accountservice.domain.entity.LoginAttemptEntry;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class LoginAttemptRepositoryImplTest {

    @Autowired
    private LoginAttemptEntryJpaRepository jpaRepo;

    @Test
    void saveAndFindById() {
        final LoginAttemptRepository repo = new LoginAttemptRepositoryImpl(jpaRepo);

        final LoginAttemptEntry attempt = new LoginAttemptEntry(
                null,
                "user-xyz",
                "10.0.2.3",
                "userA",
                LocalDateTime.now(),
                false
        );
        final LoginAttemptEntry saved = repo.save(attempt);
        assertThat(saved.getId()).isNotNull();

        final Optional<LoginAttemptEntry> loaded = repo.findById(saved.getId());
        assertThat(loaded).isPresent()
                .get()
                .isEqualTo(saved);
    }
}
