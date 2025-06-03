package ru.katacademy.bank_app.accountservice.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import ru.katacademy.bank_shared.kafka.KafkaProducer;

/**
 * Конфигурация Kafka: регистрирует в контексте Spring бин для отправки сообщений.
 */
@Configuration
public class KafkaConfig {

    /**
     * Создаёт и настраивает бин {@link KafkaProducer} для работы с {@link KafkaTemplate}.
     *
     * @param kafkaTemplate автоматически сконфигурированный шаблон для работы с Kafka
     * @return готовый к использованию продюсер сообщений
     */
    @Bean
    public KafkaProducer kafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        return new KafkaProducer(kafkaTemplate);
    }
}
