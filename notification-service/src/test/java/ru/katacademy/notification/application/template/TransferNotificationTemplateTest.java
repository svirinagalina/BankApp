package ru.katacademy.notification.application.template;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransferNotificationTemplateTest {

    private TransferNotificationTemplate template;

    @BeforeEach
    void setUp() {
        template = new TransferNotificationTemplate();
    }

    @Test
    void shouldPrepareCorrectTransferMessage() {
        String username = "Ivan";
        String amount = "1000₽";
        String recipient = "Petr";

        String expected = "Уважаемый(ая) Ivan, ваш перевод в размере 1000₽ успешно отправлен получателю Petr.";
        String actual = template.transferMessage(username, amount, recipient);

        assertEquals(expected, actual);
    }
}


