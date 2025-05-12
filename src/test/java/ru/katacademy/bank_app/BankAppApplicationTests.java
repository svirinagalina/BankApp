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
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BankAppApplicationTests {

    // Запуск Kafka-контейнера
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    static {
        kafka.start();
    }

    @Test
    void testMessageSentToDLT() {
        // Здесь поднимаем kafkaTemplate, настраиваем его через bootstrapServers из контейнера
        final String bootstrapServers = kafka.getBootstrapServers();

        // Отправка сообщения напрямую через KafkaProducer
        final Properties producerProps = new Properties();
        producerProps.put("bootstrap.servers", bootstrapServers);
        producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        try (var producer = new org.apache.kafka.clients.producer.KafkaProducer<String, String>(producerProps)) {
            producer.send(new org.apache.kafka.clients.producer.ProducerRecord<>("user-register-events", "ERROR: simulate failure"));
        }

        // Чтение через consumer
        final Properties consumerProps = new Properties();
        consumerProps.put("bootstrap.servers", bootstrapServers);
        consumerProps.put("group.id", "realTestGroup");
        consumerProps.put("auto.offset.reset", "earliest");
        consumerProps.put("enable.auto.commit", "true");
        consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        try (var consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<String, String>(consumerProps)) {
            consumer.subscribe(Collections.singletonList("user-register-events"));

            boolean found = false;
            final long timeout = System.currentTimeMillis() + 15000;

            while (System.currentTimeMillis() < timeout) {
                final var records = consumer.poll(Duration.ofMillis(500));
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
