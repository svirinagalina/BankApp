package ru.katacademy.bank_app;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BankAppApplicationTests {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    private final String bootstrapServers = "localhost:9094"; // Kafka должен быть запущен заранее
    private final String topic = "user-registration.error";

    @Test
    void testMessageSentToDLT() {

        // Отправляем сообщение с ошибкой
        kafkaTemplate.send("user-register-events", "ERROR: simulate failure");

        // Настройки consumer
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "realTestGroup");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        // Чтение из топика
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(topic));

            boolean found = false;
            final long timeout = System.currentTimeMillis() + 15000;

            while (System.currentTimeMillis() < timeout) {
                final ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
                found = StreamSupport.stream(records.spliterator(), false)
                        .anyMatch(record -> record.value().contains("ERROR"));

                if (found) {
                    break;
                }
            }
            assertThat(found).isTrue();
        }
    }

    @Test
    void contextLoads() {
    }


}
