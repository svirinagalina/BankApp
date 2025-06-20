package ru.katacademy.bank_app.accountservice.infrastructure.messaging;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.katacademy.bank_shared.event.UserRegisterEvent;
import ru.katacademy.bank_shared.kafka.KafkaProducer;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тест проверяет, что вызывается метод send у KafkaProduсer с корректным топиком и сообщением
 */

public class KafkaUserRegisterEventPublisherTest {
    //
    @Test
    void publish_ShouldSendMessageToKafkaTopic() {
        // given
        KafkaProducer kafkaProducer = mock(KafkaProducer.class);
        KafkaUserRegisterEventPublisher publisher = new KafkaUserRegisterEventPublisher(kafkaProducer);

        long userId = 1L;
        String fullName = "Ivan Ivanov";
        String email = "Ivan@gmail.com";
        LocalDateTime createdAt = LocalDateTime.of(2025, 6, 19, 10, 0);

        UserRegisterEvent event = new UserRegisterEvent(userId, fullName, email, createdAt);

        // when
        publisher.publish(event);

        // then
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        verify(kafkaProducer).send(topicCaptor.capture(), messageCaptor.capture());

        assertThat(topicCaptor.getValue()).isEqualTo("user-register-event");
        String message = messageCaptor.getValue();

        assertThat(message).contains(fullName);
        assertThat(message).contains(email);
        assertThat(message).contains(createdAt.toString());
    }
}
