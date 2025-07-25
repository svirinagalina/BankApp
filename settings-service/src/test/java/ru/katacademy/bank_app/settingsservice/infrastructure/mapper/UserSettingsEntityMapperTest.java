package ru.katacademy.bank_app.settingsservice.infrastructure.mapper;

import org.junit.jupiter.api.Test;
import ru.katacademy.bank_app.settingsservice.domain.entity.UserSettings;
import ru.katacademy.bank_app.settingsservice.infrastructure.persistence.entity.UserSettingsEntity;

import static org.junit.jupiter.api.Assertions.*;

class UserSettingsEntityMapperTest {

    @Test
    void roundTrip() {
        final UserSettings original = UserSettings.builder()
                .userId(123L)
                .notificationEnabled(true)
                .language("en")
                .darkModeEnabled(false)
                .build();

        final UserSettingsEntity entity = UserSettingsEntityMapper.toEntity(original);
        assertEquals(123L, entity.getUserId());
        assertTrue(entity.isNotificationEnabled());
        assertEquals("en", entity.getLanguage());
        assertFalse(entity.isDarkModeEnabled());
        assertNull(entity.getCreatedAt());
        assertNull(entity.getUpdatedAt());

        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(java.time.LocalDateTime.now());
        }
        entity.setUpdatedAt(java.time.LocalDateTime.now());
        entity.setUserId(123L);

        final UserSettings back = UserSettingsEntityMapper.toDomain(entity);
        assertEquals(original.getUserId(), back.getUserId());
        assertEquals(original.isNotificationEnabled(), back.isNotificationEnabled());
        assertEquals(original.getLanguage(), back.getLanguage());
        assertEquals(original.isDarkModeEnabled(), back.isDarkModeEnabled());
    }
}
