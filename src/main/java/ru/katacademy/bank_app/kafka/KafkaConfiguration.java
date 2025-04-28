package ru.katacademy.bank_app.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация для создания топиков в Kafka.
 * Этот класс создаст топик с заданными параметрами при запуске Spring-приложения.
 */
@Configuration
public class KafkaConfiguration {
    /**
     * Создает топик с именем "course" в Kafka.
     * Этот топик будет автоматически создан при запуске приложения, если его еще не существует.
     *
     * @return {@link NewTopic} объект, который описывает новый топик в Kafka.
     *         Топик будет иметь:
     *         - 1 партицию: это часть данных, которая будет обработана отдельно. Партиции помогают масштабировать обработку данных.
     *         - 1 реплику: это копия данных для обеспечения отказоустойчивости, что важно для надежности системы.
     */

    @Bean
    public NewTopic createTransferTopic() {
        return new NewTopic("transfer-completed-events", 1, (short) 1);
    }
}
