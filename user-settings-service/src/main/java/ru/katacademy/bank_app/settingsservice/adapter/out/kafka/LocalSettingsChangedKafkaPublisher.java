package ru.katacademy.bank_app.settingsservice.adapter.out.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.katacademy.bank_app.settingsservice.application.port.out.SettingsChangedEventPublisher;
import ru.katacademy.bank_shared.event.SettingsChangedEvent;

@Component
@Profile("local")
public class LocalSettingsChangedKafkaPublisher implements SettingsChangedEventPublisher {

   private static final Logger logger = LoggerFactory.getLogger(LocalSettingsChangedKafkaPublisher.class);

    @Override
    public void publish(SettingsChangedEvent event) {
        logger.info("local settings event published: {}", event);
    }
}
