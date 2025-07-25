package ru.katacademy.notification.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.katacademy.notification.application.port.out.NotificationLogRepository;
import ru.katacademy.notification.domain.model.NotificationLogEntry;
import ru.katacademy.notification.infrastructure.mapper.NotificationLogMapper;
import ru.katacademy.notification.infrastructure.persistence.entity.NotificationLog;
import ru.katacademy.notification.infrastructure.persistence.repository.NotificationLogJpaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class NotificationLogRepositoryImpl implements NotificationLogRepository {

    private final NotificationLogJpaRepository jpa;
    private final NotificationLogMapper mapper;

    @Override
    @Transactional
    public NotificationLogEntry save(NotificationLogEntry entry) {
        NotificationLog e = mapper.toEntity(entry);
        NotificationLog saved = jpa.save(e);
        return mapper.toDomain(saved);
    }

    @Override
    public List<NotificationLogEntry> findAll() {
        return jpa.findAll()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
