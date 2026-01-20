package ru.katacademy.bank_app.accountservice.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.katacademy.bank_app.accountservice.domain.events.LoginAttemptedEvent;
import ru.katacademy.bank_app.accountservice.infrastructure.messaging.StringKafkaProducer;
import ru.katacademy.bank_shared.event.notification.PasswordChangedEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    private static final String BOOTSTRAP_SERVERS = "kafka:9092";
    private static final String KEY_SERIALIZER = StringSerializer.class.getName();

    @Bean
    public ProducerFactory<String, String> stringProducerFactory() {
        return createProducerFactory(StringSerializer.class);
    }

    @Bean
    public KafkaTemplate<String, String> stringKafkaTemplate() {
        return new KafkaTemplate<>(stringProducerFactory());
    }

    @Bean
    public ProducerFactory<String, PasswordChangedEvent> passwordChangedProducerFactory() {
        return createProducerFactory(JsonSerializer.class);
    }

    @Bean
    public KafkaTemplate<String, PasswordChangedEvent> passwordChangedKafkaTemplate() {
        return new KafkaTemplate<>(passwordChangedProducerFactory());
    }

    @Bean
    public ProducerFactory<String, LoginAttemptedEvent> loginAttemptedProducerFactory() {
        return createProducerFactory(JsonSerializer.class);
    }

    @Bean
    public KafkaTemplate<String, LoginAttemptedEvent> loginAttemptedKafkaTemplate() {
        return new KafkaTemplate<>(loginAttemptedProducerFactory());
    }

    @Bean
    public StringKafkaProducer stringKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        return new StringKafkaProducer(kafkaTemplate);
    }

    /**
     * Создает ProducerFactory с общими настройками.
     *
     * @param valueSerializerClass класс для сериализации значения
     * @return настроенный ProducerFactory
     */
    private <T> ProducerFactory<String, T> createProducerFactory(Class<?> valueSerializerClass) {
        final Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KEY_SERIALIZER);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializerClass.getName());
        return new DefaultKafkaProducerFactory<>(props);
    }
}