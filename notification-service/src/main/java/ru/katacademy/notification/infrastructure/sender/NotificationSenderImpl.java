package ru.katacademy.notification.infrastructure.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.katacademy.notification.application.sender.NotificationSender;
import ru.katacademy.notification.infrastructure.persistence.entity.NotificationLog;
import ru.katacademy.notification.infrastructure.persistence.repository.NotificationLogJpaRepository;

import java.time.LocalDateTime;

/**
 * Реализация отправки уведомлений (пока заглушка).
 */
@Component
@Profile("docker")
public class NotificationSenderImpl implements NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(NotificationSenderImpl.class);

    private final NotificationLogJpaRepository notificationLogRepository;

    public NotificationSenderImpl(NotificationLogJpaRepository notificationLogRepository) {
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
