package ru.katacademy.notification.kafka;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class KafkaSmokeTest {

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void contextLoads_andKafkaTemplateWorks() {
        assertThat(kafkaTemplate).isNotNull();
    }
}