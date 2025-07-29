package ru.katacademy.bank_app.accountservice.infrastructure.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import ru.katacademy.bank_app.accountservice.application.port.out.UserRepository;
import ru.katacademy.bank_app.accountservice.domain.entity.User;
import ru.katacademy.bank_app.accountservice.domain.enumtype.UserRole;
import ru.katacademy.bank_shared.valueobject.Email;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class UserRepositoryImplTest {

    @Autowired
    private UserJpaRepository jpaRepo;

    @Test
    void saveAndFindById() {
        final UserRepository repo = new UserRepositoryImpl(jpaRepo);

        final LocalDateTime now = LocalDateTime.now();
        final User u = new User(
                null,
                UserRole.USER,
                "Anna Example",
                new Email("anna@example.com"),
                "secretHash",
                now
        );

        final User saved = repo.save(u);
        assertThat(saved.getId()).isNotNull();

        final Optional<User> loaded = repo.findById(saved.getId());
        assertThat(loaded).isPresent()
                .get()
                .isEqualTo(saved);
    }

    @Test
    void findByEmail() {
        final UserRepository repo = new UserRepositoryImpl(jpaRepo);

        final LocalDateTime now = LocalDateTime.now();
        final User u = new User(
                null,
                UserRole.ADMIN,
                "Root User",
                new Email("root@example.com"),
                "hashRoot",
                now
        );
        final User saved = repo.save(u);

        final Optional<User> byEmail = repo.findByEmail(new Email("root@example.com"));
        assertThat(byEmail).isPresent()
                .get()
                .isEqualTo(saved);
    }
}
