package ru.katacademy.infrastructure.repository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.katacademy.domain.entity.LoginAttempt;
import ru.katacademy.domain.repository.JpaLoginAttemptAuthRepository;
import ru.katacademy.domain.repository.LoginAttemptAuthRepository;
import ru.katacademy.infrastructure.entity.JpaLoginAttempt;
import ru.katacademy.infrastructure.mapper.LoginAttemptMapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Реализация {@link LoginAttemptAuthRepository} для работы с базой данных.
 * <p>
 * Обеспечивает преобразование между domain-моделью ({@link LoginAttempt})
 * и JPA-сущностью ({@link JpaLoginAttempt}) с использованием {@link LoginAttemptMapper}.
 * Все операции чтения делегируются {@link JpaLoginAttemptAuthRepository}.
 * </p>
 *
 * <p><b>Архитектурная роль:</b></p>
 * <ul>
 *   <li>Адаптер между domain-слоем и инфраструктурой</li>
 *   <li>Реализует антикоррупционный слой для работы с JPA</li>
 * </ul>
 *
 * @author MihasBatler
 * @see LoginAttemptAuthRepository Domain-интерфейс репозитория
 */
@Repository("authStatisticsRepo")
@RequiredArgsConstructor
public class LoginAttemptAuthRepositoryImpl implements LoginAttemptAuthRepository {
    private final JpaLoginAttemptAuthRepository jpaRepository;
    private final LoginAttemptMapper mapper;

    /**
     * Находит попытки входа по ID пользователя с преобразованием в domain-модель.
     * <p><b>Поток выполнения:</b></p>
     * <ol>
     *   <li>Выполняет запрос через {@link JpaLoginAttemptAuthRepository#findByUserId(Long)}</li>
     *   <li>Преобразует каждый результат через {@link LoginAttemptMapper#toDomain(JpaLoginAttempt)}</li>
     * </ol>
     *
     * @param userId идентификатор пользователя
     * @return список domain-объектов
     */
    @Override
    public List<LoginAttempt> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Находит попытки входа за указанный период с преобразованием в domain-модель.
     * <p><b>Особенности:</b></p>
     * <ul>
     *   <li>Границы диапазона включительные</li>
     *   <li>Преобразует вызов в {@link JpaLoginAttemptAuthRepository#findByTimestampBetween}</li>
     * </ul>
     *
     * @param start начальная дата
     * @param end   конечная дата
     * @return список domain-объектов
     */
    @Override
    public List<LoginAttempt> findByTimestamp(LocalDateTime start, LocalDateTime end) {
        return jpaRepository.findByTimestampBetween(start, end).stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Находит попытки входа по статусу успешности с преобразованием в domain-модель.
     *
     * @param success искомый статус:
     *                {@code true} - успешные,
     *                {@code false} - неудачные
     * @return список domain-объектов
     */
    @Override
    public List<LoginAttempt> findBySuccess(boolean success) {
        return jpaRepository.findBySuccess(success).stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Сохраняет попытку входа с гарантией транзакционности.
     * <p><b>Выполнение:</b></p>
     * <ol>
     *   <li>Преобразует domain-объект в JPA-сущность через {@link LoginAttemptMapper#toJpa}</li>
     *   <li>Сохраняет через {@link JpaRepository#save}</li>
     *   <li>Преобразует результат обратно в domain-модель</li>
     * </ol>
     *
     * @param attempt объект для сохранения (не может быть {@code null})
     * @return сохраненный domain-объект с актуальными данными
     */
    @Override
    @Transactional
    public LoginAttempt save(LoginAttempt attempt) {
        JpaLoginAttempt jpaEntity = mapper.toJpa(attempt);
        JpaLoginAttempt saved = jpaRepository.save(jpaEntity);
        return mapper.toDomain(saved);
    }
}
