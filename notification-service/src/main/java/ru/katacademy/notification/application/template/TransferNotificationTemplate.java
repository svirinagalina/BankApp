package ru.katacademy.notification.application.template;

import org.springframework.stereotype.Component;

@Component
public class TransferNotificationTemplate {

    public String transferMessage (String username, String amount, String recipient) {
        return "Уважаемый(ая) " + username + ", ваш перевод в размере " + amount +
                " успешно отправлен получателю " + recipient + ".";
    }
}
