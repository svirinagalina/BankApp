package ru.katacademy.notification.application.template;


import org.springframework.stereotype.Component;

@Component
public class WelcomeTemplate {

    public String welcome(String username) {
        return "Добро пожаловать, " + username + "! Мы рады видеть вас в нашем банке.";
    }
}
