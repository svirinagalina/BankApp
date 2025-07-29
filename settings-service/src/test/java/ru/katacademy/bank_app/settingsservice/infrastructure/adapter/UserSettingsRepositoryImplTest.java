package ru.katacademy.bank_app.settingsservice.infrastructure.adapter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import ru.katacademy.bank_app.settingsservice.application.port.out.UserSettingsRepository;
import ru.katacademy.bank_app.settingsservice.domain.entity.UserSettings;
import ru.katacademy.bank_app.settingsservice.infrastructure.persistence.repository.UserSettingsJpaRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest(
        excludeAutoConfiguration = LiquibaseAutoConfiguration.class
)
@Import(UserSettingsRepositoryImpl.class)
class UserSettingsRepositoryImplTest {

    @Autowired
    private UserSettingsJpaRepository jpaRepo;

    @Autowired
    private UserSettingsRepository repo;

    @Test
    void saveAndFindById() {
        final UserSettings settings = UserSettings.builder()
                .userId(555L)
                .notificationEnabled(true)
                .language("ru")
                .darkModeEnabled(true)
                .build();

        final UserSettings saved = repo.save(settings);
        assertThat(saved.getUserId()).isEqualTo(555L);

        assertThat(jpaRepo.findById(555L)).isPresent();

        final Optional<UserSettings> fetched = repo.findById(555L);
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getLanguage()).isEqualTo("ru");
    }

    @Test
    void deleteById() {
        final UserSettings s = UserSettings.builder()
                .userId(777L)
                .notificationEnabled(false)
                .language("en")
                .darkModeEnabled(false)
                .build();
        repo.save(s);

        assertThat(jpaRepo.existsById(777L)).isTrue();

        repo.deleteById(777L);

        assertThat(jpaRepo.existsById(777L)).isFalse();
    }
}
