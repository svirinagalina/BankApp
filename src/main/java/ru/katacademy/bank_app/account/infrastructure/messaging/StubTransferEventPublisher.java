package ru.katacademy.bank_app.account.infrastructure.messaging;

import org.springframework.stereotype.Component;
import ru.katacademy.bank_app.account.application.port.out.TransferEventPublisher;
import ru.katacademy.bank_app.shared.event.TransferComplitedEvent;

import java.util.logging.Logger;

/**
 *
 * Заглушка, жду реализации Kafka
 */
@Component
public class StubTransferEventPublisher implements TransferEventPublisher {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void publish(TransferComplitedEvent event) {
        logger.info("[STUB] Publishing event " + event);
    }
}
