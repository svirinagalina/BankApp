package ru.katacademy.notification.infrastructure.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.katacademy.notification.application.sender.NotificationSender;

@Component
@Profile("local")
public class LocalNotificationSender implements NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(LocalNotificationSender.class);

    @Override
    public void send(String message) {
        message = "Local notification sender has been sent";
        log.info(message);
    }
}