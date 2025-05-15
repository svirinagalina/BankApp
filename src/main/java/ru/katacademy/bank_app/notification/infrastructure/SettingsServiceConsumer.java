package ru.katacademy.bank_app.notification.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.katacademy.bank_shared.event.SettingsChangedEvent;

@Component
@Slf4j
public class SettingsServiceConsumer {

    @KafkaListener(topics = "settings-changed-topic", groupId = "notification-group")
    public void onSettingsChanged(SettingsChangedEvent event) {
        System.out.println("Received new settings: " + event);

        //TODO - реализовать изменение настроек нотификоции у конкретного пользователя
    }
}
