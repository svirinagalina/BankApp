package ru.katacademy.notification.application.sender;

import org.springframework.stereotype.Component;

@Component
public class NotificationSenderImpl  implements NotificationSender {
    @Override
    public void send(String message) {
        System.out.println(" Отправлено на email: " + message);
    }
}
