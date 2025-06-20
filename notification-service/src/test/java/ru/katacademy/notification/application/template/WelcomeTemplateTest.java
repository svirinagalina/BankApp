package ru.katacademy.notification.application.template;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WelcomeTemplateTest {

    @Test
    void shouldReturnCorrectWelcomeMessage() {
        // given - шаблон для имя пользователя (который задаем)
        WelcomeTemplate template = new WelcomeTemplate();
        String username = "Иван";

        // вызов метода
        String message = template.welcome(username);

        // сравнение результатов
        String expected = "Добро пожаловать, Иван! Мы рады видеть вас в нашем банке.";
        assertEquals(expected, message);
    }
}
