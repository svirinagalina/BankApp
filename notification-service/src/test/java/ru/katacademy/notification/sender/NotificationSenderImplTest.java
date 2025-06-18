package ru.katacademy.notification.sender;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.katacademy.notification.infrastructure.sender.NotificationSenderImpl;
import ru.katacademy.notification.infrastructure.persistence.entity.NotificationLog;
import ru.katacademy.notification.infrastructure.persistence.repository.NotificationLogRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class NotificationSenderImplTest {

    @Test
    void send_shouldLogMessageAndSaveToRepository() {
        NotificationLogRepository mockRepo = mock(NotificationLogRepository.class);
        NotificationSenderImpl sender = new NotificationSenderImpl(mockRepo);

        String message = "Test message";

        sender.send(message);


        ArgumentCaptor<NotificationLog> captor = ArgumentCaptor.forClass(NotificationLog.class);
        verify(mockRepo, times(1)).save(captor.capture());

        NotificationLog savedLog = captor.getValue();
        assertThat(savedLog.getMessage()).isEqualTo(message);
        assertThat(savedLog.getTimestamp()).isNotNull();
        assertThat(savedLog.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }
}

