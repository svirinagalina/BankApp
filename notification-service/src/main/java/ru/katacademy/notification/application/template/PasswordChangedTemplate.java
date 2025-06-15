package ru.katacademy.notification.application.template;

import org.springframework.stereotype.Component;

@Component
public class PasswordChangedTemplate {
    public String passwordChangedMessage(String username) {
        return "Здравствуйте, " + username + ". Ваш пароль был успешно изменён.";
    }
}
