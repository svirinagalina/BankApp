package ru.katacademy.notification.application.port.out;

import ru.katacademy.notification.domain.model.NotificationLogEntry;
import java.util.List;

public interface NotificationLogRepository {
    NotificationLogEntry save(NotificationLogEntry entry);
    List<NotificationLogEntry> findAll();
}
