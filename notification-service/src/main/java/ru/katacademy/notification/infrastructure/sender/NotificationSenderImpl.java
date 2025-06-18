package ru.katacademy.notification.infrastructure.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.katacademy.notification.application.sender.NotificationSender;
import ru.katacademy.notification.infrastructure.persistence.entity.NotificationLog;
import ru.katacademy.notification.infrastructure.persistence.repository.NotificationLogRepository;

import java.time.LocalDateTime;

/**
 * Реализация отправки уведомлений (пока заглушка).
 */
public class NotificationSenderImpl implements NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(NotificationSenderImpl.class);

    private final NotificationLogRepository notificationLogRepository;

    public NotificationSenderImpl(NotificationLogRepository notificationLogRepository) {
        this.notificationLogRepository = notificationLogRepository;
    }

    @Override
    public void send(String message) {
        // TODO: Реализовать реальную отправку (по email/SMS и т.д.)
        log.info("Отправка уведомления: {}", message);

        NotificationLog logEntry = new NotificationLog(message, LocalDateTime.now());
        notificationLogRepository.save(logEntry);
    }
}
