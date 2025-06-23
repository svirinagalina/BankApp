package ru.katacademy.bank_app.frauddetection.health;

import lombok.experimental.Delegate;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
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
     * Источник соединений к базе данных (неизменяемая обертка).
     */
    private final DataSource dataSource;

    /**
     * Конструктор.
     *
     * @param dataSource источник соединений {@link DataSource}
     */
    public DatabaseHealthIndicator(DataSource dataSource) {
        this.dataSource = new ImmutableDataSourceWrapper(dataSource);
    }

    /**
     * Проверяет состояние базы данных.
     *
     * <p>Получает соединение из пула и вызывает {@link Connection#isValid(int)} с таймаутом 1 секунда.</p>
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

    /**
     * Неизменяемая обертка для DataSource, предотвращающая модификацию исходного объекта.
     */
    private static final class ImmutableDataSourceWrapper implements DataSource {

        @Delegate(excludes = MutableMethods.class)
        private final DataSource delegate;

        interface MutableMethods {
            void setLoginTimeout(int seconds);
            void setLogWriter(PrintWriter out);
        }

        ImmutableDataSourceWrapper(DataSource delegate) {
            this.delegate = delegate;
        }

        @Override
        public void setLoginTimeout(int seconds) {
            throw new UnsupportedOperationException("Immutable DataSource: setLoginTimeout not allowed");
        }

        @Override
        public void setLogWriter(PrintWriter out) {
            throw new UnsupportedOperationException("Immutable DataSource: setLogWriter not allowed");
        }
    }
}