package ru.katacademy.notification.application.template;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PasswordChangedTemplateTest {

    private PasswordChangedTemplate template;

    @BeforeEach
    void setUp() {
        template = new PasswordChangedTemplate();
    }

    @Test
    void shouldPrepareCorrectPasswordChangedMessage() {
        String username = "Maria";
        String expected = "Здравствуйте, Maria. Ваш пароль был успешно изменён.";
        String actual = template.passwordChangedMessage(username);

        assertEquals(expected, actual);
    }
}
