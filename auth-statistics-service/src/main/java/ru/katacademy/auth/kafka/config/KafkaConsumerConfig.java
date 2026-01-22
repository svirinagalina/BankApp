package ru.katacademy.auth.kafka.config;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import ru.katacademy.bank_shared.event.notification.PasswordChangedEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Конфигурация Kafka-потребителя для обработки событий смены пароля пользователя.
 * <p>
 * Настраивает фабрику consumer'ов для получения Avro-сообщений типа {@link PasswordChangedEvent}.
 * Сообщения приходят из топика Kafka {@code password-events} и десериализуются с помощью Avro.
 * </p>
 *
 * <p>
 * <b>Конфигурация consumer:</b>
 * <ul>
 *   <li>Bootstrap-серверы: берутся из свойства {@code spring.kafka.bootstrap-servers}</li>
 *   <li>Группа consumer'ов: {@code auth-statistic-service-group}</li>
 *   <li>Десериализация ключа: {@link StringDeserializer}</li>
 *   <li>Десериализация значения: {@link KafkaAvroDeserializer}</li>
 *   <li>URL Schema Registry: {@code http://localhost:9091}</li>
 *   <li>Используется specific Avro reader, чтобы получать {@link PasswordChangedEvent}, а не GenericRecord</li>
 * </ul>
 * </p>
 *
 * <p>
 * После настройки фабрики consumer'ов, бин {@link ConcurrentKafkaListenerContainerFactory} позволяет
 * аннотированным методам {@link org.springframework.kafka.annotation.KafkaListener} автоматически
 * получать события и обрабатывать их.
 * </p>
 *
 * author: Krasirskii Dmitrii
 * date: 14.01.2026
 */
@Configuration
public class KafkaConsumerConfig {

    /**
     * Адрес Kafka bootstrap-серверов, берется из application.properties
     */
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Создает фабрику consumerов для получения событий смены пароля.
     * <p>
     * ConsumerFactory настраивается на десериализацию ключа как String
     * и значения как Avro-конкретный класс {@link PasswordChangedEvent}.
     * </p>
     *
     * @return {@link ConsumerFactory} для {@code PasswordChangedEvent}
     */
    @Bean
    public ConsumerFactory<String, PasswordChangedEvent> consumerFactory() {
        final Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "auth-statistic-service-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put("schema.registry.url", "http://localhost:9091");
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    /**
     * Создает фабрику контейнеров для KafkaListener.
     * <p>
     * Использует {@link #consumerFactory()} для получения consumer'ов и
     * позволяет аннотированным методам {@link org.springframework.kafka.annotation.KafkaListener}
     * автоматически подписываться на топики и получать события.
     * </p>
     *
     * @return {@link ConcurrentKafkaListenerContainerFactory} для {@link PasswordChangedEvent}
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PasswordChangedEvent> kafkaListenerContainerFactory() {
        final ConcurrentKafkaListenerContainerFactory<String, PasswordChangedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}