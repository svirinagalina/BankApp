package ru.katacademy.bank_app.accountservice.infrastructure.messaging;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.katacademy.bank_shared.event.UserRegisterEvent;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тест проверяет работу методов KafkaUserRegisterEventPublisher
 * Тест publish_ShouldSendMessageToKafkaTopic имитирует ситуацию, когда пользователь регистрируется в системе.
 * Проверяет, что метод send у KafkaProduсer вызывается с корректным топиком и сообщением.
 */
class KafkaUserRegisterEventPublisherTest {
    @Test
    void publish_ShouldSendMessageToKafkaTopic() {

        final StringKafkaProducer stringKafkaProducer = mock(StringKafkaProducer.class);
        final KafkaUserRegisterEventPublisher publisher = new KafkaUserRegisterEventPublisher(stringKafkaProducer);

        final long userId = 1L;
        final String fullName = "Ivan Ivanov";
        final String email = "Ivan@gmail.com";
        final LocalDateTime createdAt = LocalDateTime.of(2025, 6, 19, 10, 0);

        final UserRegisterEvent event = new UserRegisterEvent(userId, fullName, email, createdAt);

        publisher.publish(event);

        final ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        verify(stringKafkaProducer).send(topicCaptor.capture(), messageCaptor.capture());

        assertThat(topicCaptor.getValue()).isEqualTo("user-register-event");
        final String message = messageCaptor.getValue();

        assertThat(message)
                .contains(fullName)
                .contains(email)
                .contains(createdAt.toString());
    }
}
