package ru.katacademy.bank_app.settingsservice.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * JPA‑сущность для хранения пользовательских настроек.
 * Соответствует таблице {@code user_settings}.
 */
@Entity
@Table(name = "user_settings")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserSettingsEntity {

    @Id
    private Long userId;
    private boolean notificationEnabled;
    private String language;
    private boolean darkModeEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * JPA‑callback перед первым сохранением: устанавливаем createdAt и updatedAt.
     */
    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    /**
     * JPA‑callback перед обновлением: обновляем updatedAt.
     */
    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
