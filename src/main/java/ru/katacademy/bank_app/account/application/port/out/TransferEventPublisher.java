package ru.katacademy.bank_app.account.application.port.out;

import ru.katacademy.bank_app.shared.event.TransferComplitedEvent;

/**
 * Интерфейс для публикации событий о переводах.
 * @author Sheffy
 */
public interface TransferEventPublisher {
    void publish(TransferComplitedEvent event);
}
