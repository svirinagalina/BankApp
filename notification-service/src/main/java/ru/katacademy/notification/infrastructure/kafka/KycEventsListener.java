package ru.katacademy.notification.infrastructure.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.katacademy.bank_shared.enums.KycStatus;
import ru.katacademy.bank_shared.event.kyc.KycStatusChangedEvent;

/**
 * Kafka-слушатель событий KYC.
 * <p>
 * Сообщение десериализуется в {@link KycStatusChangedEvent}; ключ сообщения — {@code userId} (String).
 */
@Component
public class KycEventsListener {
    private static final Logger log = LoggerFactory.getLogger(KycEventsListener.class);

    /**
     * Обрабатывает событие смены KYC-статуса пользователя.
     * <p>
     * Логирует получение события; для статусов {@link KycStatus#REJECTED} и
     * {@link KycStatus#PENDING_RETRY} пишет предупреждающий лог.
     *
     * @param event событие смены KYC-статуса
     */
    @KafkaListener(topics = "${kyc.topics.events}", groupId = "notification-service")
    public void onKycEvent(KycStatusChangedEvent event) {
        log.info("KYC event received: userId={}, status={}, ts={}, source={}",
                event.userId(), event.status(), event.timestamp(), event.source());

        if (event.status() == KycStatus.REJECTED || event.status() == KycStatus.PENDING_RETRY) {
            log.warn("KYC attention: userId={}, status={}, ts={}",
                    event.userId(), event.status(), event.timestamp());
        }
    }
}
