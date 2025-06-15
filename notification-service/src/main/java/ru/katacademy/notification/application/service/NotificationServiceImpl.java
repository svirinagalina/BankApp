package ru.katacademy.notification.application.service;

import org.springframework.stereotype.Service;
import ru.katacademy.bank_shared.event.notification.PasswordChangedEvent;
import ru.katacademy.bank_shared.event.notification.TransferCompletedEvent;
import ru.katacademy.bank_shared.event.notification.UserRegisteredEvent;
import ru.katacademy.notification.application.sender.NotificationSender;
import ru.katacademy.notification.application.template.PasswordChangedTemplate;
import ru.katacademy.notification.application.template.TransferNotificationTemplate;
import ru.katacademy.notification.application.template.WelcomeTemplate;

@Service
public class NotificationServiceImpl implements NotificationService {

    private WelcomeTemplate welcomeTemplate;
    private PasswordChangedTemplate passwordChangedTemplate;
    private TransferNotificationTemplate transferNotificationTemplate;
    private NotificationSender notificationSender;

    // Внедрение зависимостей, для методов
    public NotificationServiceImpl(PasswordChangedTemplate passwordChangedTemplate, TransferNotificationTemplate transferNotificationTemplate
            , WelcomeTemplate welcomeTemplate, NotificationSender notificationSender) {

        this.passwordChangedTemplate = passwordChangedTemplate;
        this.transferNotificationTemplate = transferNotificationTemplate;
        this.welcomeTemplate = welcomeTemplate;
        this.notificationSender = notificationSender;

    }


    @Override
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        String text = welcomeTemplate.welcome(event.getUsername());
        notificationSender.send(text);

    }

    @Override
    public void handleTransferCompletedEvent(TransferCompletedEvent event) {
        String text = transferNotificationTemplate.transferMessage(event.getUsername(), event.getAmount(), event.getRecipient());
        notificationSender.send(text);


    }

    @Override
    public void handlePasswordChangedEvent(PasswordChangedEvent event) {
        String text = passwordChangedTemplate.passwordChangedMessage(event.getUsername());
        notificationSender.send(text);

    }

}
