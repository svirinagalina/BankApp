package ru.katacademy.bank_app.accountservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.katacademy.bank_shared.enums.KycStatus;
import ru.katacademy.bank_shared.event.kyc.KycStatusChangedEvent;

/**
 * Kafka-слушатель событий KYC для account-service.
 * Подписывается на {@code kyc-events}, логирует все статусы.
 */
@Component
public class KycEventsListener {
    private static final Logger log = LoggerFactory.getLogger(KycEventsListener.class);

    @KafkaListener(topics = "${kyc.topics.events:kyc-events}", groupId = "account-service")
    public void onKycEvent(KycStatusChangedEvent e) {
        log.info("KYC event (account): userId={}, status={}, ts={}, source={}",
                e.userId(), e.status(), e.timestamp(), e.source());

        if (e.status() == KycStatus.REJECTED || e.status() == KycStatus.PENDING_RETRY) {
            log.warn("KYC attention (account): userId={}, status={}, ts={}",
                    e.userId(), e.status(), e.timestamp());
        }
    }
}