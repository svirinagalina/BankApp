package ru.katacademy.bank_app.frauddetection;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционный тест для Kafka-потребителя FraudDetectionConsumer.
 * <p>
 * Проверяет, что при отправке сообщения в топик transfer-completed-events,
 * consumer принимает и обрабатывает его.
 *
 * Автор: Быстров М.М.
 *
 * Дата: 02.07.2025
 */
@SpringBootTest(properties = {
        "spring.profiles.active=test",
        // Точка входа клиента/шаблона — наш EmbeddedKafka-брокер
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        // Если у вас вдруг остались смещённые оффсеты, всегда начинать с earliest
        "spring.kafka.consumer.auto-offset-reset=earliest"
})
@EmbeddedKafka(partitions = 1, topics = {"transfer-completed-events"})
@DirtiesContext
public class FraudDetectionKafkaIntegrationTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final AtomicReference<String> receivedMessage = new AtomicReference<>();
    private static final CountDownLatch latch = new CountDownLatch(1);

    /**
     * Локальный consumer — обрабатывает сообщение из Kafka и сигнализирует о приёме.
     */
    @KafkaListener(topics = "transfer-completed-events", groupId = "fraud-test-group")
    public void listenTest(String message) {
        receivedMessage.set(message);
        latch.countDown();
    }

    /**
     * Проверяет, что consumer корректно получает сообщение из топика Kafka.
     */
    @Test
    void shouldConsumeKafkaMessage() throws Exception {
        final String testPayload = "TRANSFER_COMPLETED:" + System.currentTimeMillis();

        kafkaTemplate.send("transfer-completed-events", testPayload);

        final boolean consumed = latch.await(5, TimeUnit.SECONDS);

        assertThat(consumed)
                .as("Consumer должен принять сообщение из Kafka")
                .isTrue();

        assertThat(receivedMessage.get())
                .as("Consumer должен получить корректное сообщение")
                .isEqualTo(testPayload);
    }
}
