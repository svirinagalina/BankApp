package ru.katacademy.bank_app.frauddetection.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.katacademy.bank_shared.enums.KycStatus;
import ru.katacademy.bank_shared.event.kyc.KycStatusChangedEvent;

/**
 * Kafka-слушатель событий KYC для сервиса fraud-detection.
 * <p>
 * Подписывается на топик {@code kyc-events},
 * логирует все полученные статусы и выводит предупреждающий лог для
 * {@link KycStatus#REJECTED} и {@link KycStatus#PENDING_RETRY}.
 */
@Component
public class KycEventsConsumer {

    private static final Logger log = LoggerFactory.getLogger(KycEventsConsumer.class);

    @KafkaListener(topics = "${kyc.topics.events:kyc-events}", groupId = "fraud-detection")
    public void onKycEvent(KycStatusChangedEvent e) {
        log.info("KYC event (fraud): userId={}, status={}, ts={}, source={}",
                e.userId(), e.status(), e.timestamp(), e.source());

        if (e.status() == KycStatus.REJECTED || e.status() == KycStatus.PENDING_RETRY) {
            log.warn("KYC attention (fraud): userId={}, status={}, ts={}",
                    e.userId(), e.status(), e.timestamp());
        }
    }
}