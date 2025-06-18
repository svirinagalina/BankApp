package ru.katacademy.notification.application.template;


public class PasswordChangedTemplate {
    public String passwordChangedMessage(String username) {
        return "Здравствуйте, " + username + ". Ваш пароль был успешно изменён.";
    }
}
