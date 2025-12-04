package ru.katacademy.bank_app.settingsservice.application.port.out;

import ru.katacademy.bank_shared.event.SettingsChangedEvent;

public interface SettingsChangedEventPublisher {
    void publish(SettingsChangedEvent event);
}
