package ru.katacademy.bank_app.accountservice.application.port.out;

import ru.katacademy.bank_shared.event.TransferCompletedEvent;

public interface TransferEventPublisher {
    void publish(TransferCompletedEvent event);
}
