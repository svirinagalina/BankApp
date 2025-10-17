package ru.katacademy.kycservice.infrastructure.kafka.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.katacademy.bank_shared.event.kyc.KycStatusChangedEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    private final Environment env;

    public KafkaConfig(Environment env) {
        this.env = env;
    }

    private String resolveBootstrapServers() {
        String bootstrap = env.getProperty("spring.kafka.bootstrap-servers");
        if (bootstrap == null || bootstrap.isBlank()) {
            bootstrap = env.getProperty("SPRING_KAFKA_BOOTSTRAP_SERVERS");
        }

        if (bootstrap == null || bootstrap.isBlank()) {
            throw new IllegalStateException(
                    "Kafka bootstrap servers are not set. " +
                            "Provide spring.kafka.bootstrap-servers or SPRING_KAFKA_BOOTSTRAP_SERVERS (e.g., 'kafka:9092')."
            );
        }
        return bootstrap;
    }

    private Map<String, Object> baseProducerProps() {
        Map<String, Object> props = new HashMap<>(8);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, resolveBootstrapServers());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 10);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 5);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 32_768); // 32 KB
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1); // чтобы избежать реордеринга при ретраях
        return props;
    }

    @Bean
    public ProducerFactory<String, String> stringProducerFactory() {
        Map<String, Object> props = baseProducerProps();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, String> stringKafkaTemplate(ProducerFactory<String, String> pf) {
        return new KafkaTemplate<>(pf);
    }

    @Bean
    public ProducerFactory<String, KycStatusChangedEvent> kycEventProducerFactory() {
        Map<String, Object> props = baseProducerProps();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, KycStatusChangedEvent> kycEventKafkaTemplate(
            ProducerFactory<String, KycStatusChangedEvent> pf) {
        return new KafkaTemplate<>(pf);
    }
}
