package ru.katacademy.bank_app.settingsservice.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Сущность настроек пользователя.
 * <p>
 * Хранит персональные настройки пользователя, такие как язык интерфейса,
 * тема оформления и параметры уведомлений.
 * </p>
 * @author Sheffy
 */
@Entity
@Table(name = "user_settings")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserSettings {

    /**
     * Уникальный идентификатор пользователя
     */
    @Id
    private long userId;

    /**
     * Флаг включения уведомлений
     */
    private boolean notificationEnabled;

    /**
     * Язык интерфейса
     */
    private String language;

    /**
     * Флаг темной темы интерфейса
     */
    private boolean darkModeEnabled;

    /**
     * Дата и время создания настроек
     */
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего обновления настроек
     */
    private LocalDateTime updatedAt;

    /**
     * JPA callback-метод, вызываемый перед созданием новой записи.
     * Устанавливает даты создания и обновления.
     */
    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * JPA callback-метод, вызываемый перед обновлением записи.
     * Обновляет дату изменения.
     */
    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}