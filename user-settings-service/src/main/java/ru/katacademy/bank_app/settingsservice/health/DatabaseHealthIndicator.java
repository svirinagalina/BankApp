package ru.katacademy.bank_app.settingsservice.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * HealthIndicator для проверки доступности базы данных.
 * <p>
 * Выполняет попытку получения соединения из {@link DataSource} и проверяет его валидность.
 * Возвращает статус {@code UP}, если соединение валидно, иначе — {@code DOWN} с деталями ошибки.
 * </p>
 *
 * <p>
 * Активен только вне профиля {@code test}.
 * </p>
 */
@Component
@Profile("!test")
public class DatabaseHealthIndicator implements HealthIndicator {

    /**
     * Источник соединений к базе данных.
     */
    private final DataSource dataSource;

    /**
     * Конструктор.
     *
     * @param dataSource источник соединений {@link DataSource}
     */
    public DatabaseHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Проверяет состояние базы данных.
     *
     * <p>Получает соединение из пула и вызывает {@link java.sql.Connection#isValid(int)} с таймаутом 1 секунда.</p>
     *
     * @return {@link Health#up()} если соединение валидно,
     *         иначе {@link Health#down()} с сообщением об ошибке в деталях.
     */
    @Override
    public Health health() {
        try (var conn = dataSource.getConnection()) {
            final boolean isValid = conn.isValid(1);
            if (isValid) {
                return Health.up().build();
            } else {
                return Health.down().build();
            }
        } catch (SQLException e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
