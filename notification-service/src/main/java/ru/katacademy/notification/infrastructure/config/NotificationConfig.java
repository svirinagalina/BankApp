package ru.katacademy.notification.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.katacademy.notification.application.sender.NotificationSender;
import ru.katacademy.notification.application.service.NotificationService;
import ru.katacademy.notification.application.service.NotificationServiceImpl;
import ru.katacademy.notification.application.template.PasswordChangedTemplate;
import ru.katacademy.notification.application.template.TransferNotificationTemplate;
import ru.katacademy.notification.application.template.WelcomeTemplate;
import ru.katacademy.notification.infrastructure.persistence.repository.NotificationLogJpaRepository;
import ru.katacademy.notification.infrastructure.sender.NotificationSenderImpl;

@Configuration
public class NotificationConfig {

    @Bean
    public NotificationService notificationService(
            WelcomeTemplate welcomeTemplate,
            PasswordChangedTemplate passwordChangedTemplate,
            TransferNotificationTemplate transferNotificationTemplate,
            NotificationSender notificationSender
    ) {
        return new NotificationServiceImpl(passwordChangedTemplate, transferNotificationTemplate, welcomeTemplate, notificationSender);
    }

    @Bean
    public NotificationSender notificationSender(NotificationLogJpaRepository notificationLogRepository) {
        return new NotificationSenderImpl(notificationLogRepository);
    }
}

