package ru.katacademy.bank_app.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaConsumer {

    @KafkaListener(topics = "course", groupId = "my_consumer")
    public void listen(String message) {
        log.info("Received message = " + message);
    }
}
