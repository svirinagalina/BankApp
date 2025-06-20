package ru.katacademy.notification.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.katacademy.notification.application.template.WelcomeTemplate;
import ru.katacademy.notification.application.template.PasswordChangedTemplate;
import ru.katacademy.notification.application.template.TransferNotificationTemplate;

@Configuration
public class NotificationTemplateConfig {

    @Bean
    public WelcomeTemplate welcomeTemplate() {
        return new WelcomeTemplate();
    }

    @Bean
    public PasswordChangedTemplate passwordChangedTemplate() {
        return new PasswordChangedTemplate();
    }

    @Bean
    public TransferNotificationTemplate transferNotificationTemplate() {
        return new TransferNotificationTemplate();
    }
}

