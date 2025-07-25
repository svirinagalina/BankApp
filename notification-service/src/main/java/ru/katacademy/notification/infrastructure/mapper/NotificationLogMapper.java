package ru.katacademy.notification.infrastructure.mapper;

import org.springframework.stereotype.Component;
import ru.katacademy.notification.domain.model.NotificationLogEntry;
import ru.katacademy.notification.infrastructure.persistence.entity.NotificationLog;

@Component
public class NotificationLogMapper {

    public NotificationLog toEntity(NotificationLogEntry d) {
        NotificationLog e = new NotificationLog(d.getMessage(), d.getTimestamp());
        e.setId(d.getId());
        return e;
    }

    public NotificationLogEntry toDomain(NotificationLog e) {
        return new NotificationLogEntry(e.getId(), e.getMessage(), e.getTimestamp());
    }
}
