package ru.katacademy.bank_app.accountservice.infrastructure.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.katacademy.bank_shared.event.TransferCompletedEvent;
import ru.katacademy.bank_shared.kafka.KafkaProducer;
import ru.katacademy.bank_shared.valueobject.AccountNumber;
import ru.katacademy.bank_shared.valueobject.Currency;
import ru.katacademy.bank_shared.valueobject.Money;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Тест лоя проверки работы методов KafkaTransferEventPublisher
 */
@ExtendWith(MockitoExtension.class)
class KafkaTransferEventPublisherTest {

    @Mock
    private KafkaProducer kafkaProducer;
    private KafkaTransferEventPublisher publisher;

    private final Currency rub = new Currency("RUB", "Russian Ruble", 2);

    @BeforeEach
    void setUp() {
        publisher = new KafkaTransferEventPublisher(kafkaProducer);
        ReflectionTestUtils.setField(publisher, "topic", "transfer-completed-events");
    }

    @Test
    void publish_withTopicKeyAndMessage() {
        final TransferCompletedEvent event = new TransferCompletedEvent(
                UUID.randomUUID(),
                new AccountNumber("12345678901234567890"),
                new AccountNumber("09876543210987654321"),
                new Money(BigDecimal.valueOf(5_000), rub),
                LocalDateTime.now()
        );

        publisher.publish(event);

        final ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

        verify(kafkaProducer, times(1)).send(topicCaptor.capture(), keyCaptor.capture(), valueCaptor.capture());

        assertEquals("transfer-completed-events", topicCaptor.getValue());
        assertEquals(event.eventId().toString(), keyCaptor.getValue());
        assertNotNull(valueCaptor.getValue());
        assertTrue(valueCaptor.getValue().contains(event.eventId().toString()));
    }
}
