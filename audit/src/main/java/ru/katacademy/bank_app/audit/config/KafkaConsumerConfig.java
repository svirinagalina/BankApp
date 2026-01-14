package ru.katacademy.bank_app.audit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import ru.katacademy.bank.events.password.v1.PasswordChangedEvent;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PasswordChangedEvent>
    kafkaListenerContainerFactory(ConsumerFactory<String, PasswordChangedEvent> consumerFactory) {
        final ConcurrentKafkaListenerContainerFactory<String, PasswordChangedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}
