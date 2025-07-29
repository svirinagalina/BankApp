package ru.katacademy.notification.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.katacademy.notification.infrastructure.persistence.entity.NotificationLog;

/**
 * Репозиторий для сохранения логов уведомлений.
 */
public interface NotificationLogJpaRepository extends JpaRepository<NotificationLog, Long> {
}

