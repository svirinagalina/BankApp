package ru.katacademy.bank_app.settingsservice.adapter.out.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.katacademy.bank_app.settingsservice.application.port.out.SettingsChangedEventPublisher;
import ru.katacademy.bank_shared.event.SettingsChangedEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class SettingsChangedKafkaPublisher implements SettingsChangedEventPublisher {

    private final KafkaTemplate<String, SettingsChangedEvent> kafkaTemplate;


    @Override
    public void publish(SettingsChangedEvent event) {
        kafkaTemplate.send("settings-changed-topic", event.userId().toString(), event);
        log.info("Published settings change for user {}", event.userId());
    }
}
