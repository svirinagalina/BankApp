package ru.katacademy.bank_app.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

/**
 * Конфигурация обработки ошибок для Kafka потребителей.
 * <p>
 * Настраивает механизм Dead Letter Topic (DLT) для обработки неудачных сообщений
 * и политику повторных попыток.
 * </p>
 *
 * @author Sheffy
 */
@Slf4j
@Configuration
public class KafkaErrorHandlerConfig {

    /**
     * Создает DeadLetterPublishingRecoverer для отправки неудачных сообщений в DLT.
     *
     * @param kafkaTemplate Kafka-шаблон для отправки сообщений
     * @return настроенный DeadLetterPublishingRecoverer
     * @throws IllegalArgumentException если kafkaTemplate == null
     */
    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(KafkaTemplate<Object, Object> kafkaTemplate) {
        // Настройка, чтобы при ошибке отправлялось в .error топик
        return new DeadLetterPublishingRecoverer(kafkaTemplate, (record, ex) -> {
            final String originalTopic = record.topic();
            final String dltTopic = switch (originalTopic) {
                case "transfer-completed-events" -> "transfer.error";
                case "user-register-events" -> "user-registration.error";
                default -> "audit.error";
            };
            return new TopicPartition(dltTopic, record.partition());
        });
    }

    /**
     * Создает обработчик ошибок с политикой однократного повтора.
     * <p>
     * После неудачной попытки сообщение отправляется в DLT.
     * </p>
     *
     * @param recoverer DeadLetterPublishingRecoverer для обработки DLT
     * @return настроенный DefaultErrorHandler
     */
    @Bean
    public DefaultErrorHandler errorHandler(DeadLetterPublishingRecoverer recoverer) {
        // 1 попытка, потом DLT
        final DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(0L, 1));

        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            log.error("Ошибка обработки сообщения [топик-%s, offset=%d]: %s%n",
                    record.topic(), record.offset(), ex.getMessage());
        });
        return errorHandler;
    }

    /**
     * Создает фабрику для Kafka потребителей с обработкой ошибок.
     *
     * @param consumerFactory фабрика потребителей Kafka
     * @param errorHandler обработчик ошибок
     * @return настроенная фабрика контейнеров
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory(
            ConsumerFactory<Object, Object> consumerFactory,
            DefaultErrorHandler errorHandler
    ) {
        final var factory = new ConcurrentKafkaListenerContainerFactory<Object, Object>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}
