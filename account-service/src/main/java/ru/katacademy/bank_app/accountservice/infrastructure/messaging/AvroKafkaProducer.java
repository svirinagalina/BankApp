package ru.katacademy.bank_app.accountservice.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AvroKafkaProducer {

    private final KafkaTemplate<String, SpecificRecordBase> avroKafkaTemplate;

    public void send(String topic, SpecificRecordBase message) {
        avroKafkaTemplate.send(topic, message);
    }

    public void send(String topic, String key, SpecificRecordBase message) {
        avroKafkaTemplate.send(topic, key, message);
    }
}
