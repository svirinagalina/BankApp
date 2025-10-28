package ru.katacademy.kycservice.infrastructure.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.katacademy.bank_shared.event.kyc.KycStatusChangedEvent;
import ru.katacademy.kycservice.application.port.out.KycEventPublisher;

/**
 * Публикует события смены статуса KYC
 * <p>
 * Отправляет {@link KycStatusChangedEvent} в Kafka-топик, имя которого берётся из
 * конфигурации {@code kyc.topics.events}.
 * Для ключа сообщения используется {@code userId} события, чтобы сохранять порядок сообщений по пользователю.
 * Успешная отправка и ошибки логируются.
 * Метод publish: Публикует событие смены KYC-статуса в Kafka асинхронно.
 * Дата: 17-09-2025
 * Автор: Белявский Г.А.
 */
@Component
public class KafkaKycEventPublisher implements KycEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(KafkaKycEventPublisher.class);

    private final KafkaTemplate<String, KycStatusChangedEvent> template;
    private final String topic;

    public KafkaKycEventPublisher(KafkaTemplate<String, KycStatusChangedEvent> template,
                                  @Value("${kyc.topics.events:kyc-events}") String topic) {
        this.template = template;
        this.topic = topic;
    }

    @Override
    public void publish(KycStatusChangedEvent kycStatusChangedEvent) {
        template.send(topic, kycStatusChangedEvent.userId(), kycStatusChangedEvent)
                .whenComplete((res, ex) -> {
            if (ex != null) {
                log.error("Kyc event publisher error: {}", kycStatusChangedEvent, ex);
            } else {
                var md = res.getRecordMetadata();
                log.info("Kyc event sent: topic={}, partition={}, offset={}, key={}",
                        md.topic(), md.partition(), md.offset(), kycStatusChangedEvent.userId());
            }
        });
    }
}
