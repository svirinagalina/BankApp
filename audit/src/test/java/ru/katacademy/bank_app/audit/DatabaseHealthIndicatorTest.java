package ru.katacademy.bank_app.audit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import ru.katacademy.bank_app.audit.health.DatabaseHealthIndicator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Юнит-тесты для {@link DatabaseHealthIndicator}.
 *
 * <p>Проверяется корректное поведение индикатора при успешном и неуспешном подключении к базе данных.</p>
 */
public class DatabaseHealthIndicatorTest {

    /**
     * Тест проверяет, что при валидном соединении {@link DatabaseHealthIndicator#health()}
     * возвращает статус {@link Status#UP}.
     *
     * @throws SQLException в случае ошибок мокирования
     */
    @Test
    void shouldReturnUpIfConnectionIsValid() throws SQLException {
        final DataSource dataSource = mock(DataSource.class);
        final Connection connection = mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(1)).thenReturn(true);

        final DatabaseHealthIndicator indicator = new DatabaseHealthIndicator(dataSource);

        final Health health = indicator.health();

        assertEquals(Status.UP, health.getStatus());
    }

    /**
     * Тест проверяет, что при исключении {@link SQLException} при попытке получить соединение
     * {@link DatabaseHealthIndicator#health()} возвращает статус {@link Status#DOWN}
     * и содержит деталь с описанием ошибки.
     *
     * @throws SQLException в случае ошибок мокирования
     */
    @Test
    void shouldReturnDownIfConnectionFails() throws SQLException {
        final DataSource dataSource = mock(DataSource.class);
        when(dataSource.getConnection()).thenThrow(new SQLException("DB error"));

        final DatabaseHealthIndicator indicator = new DatabaseHealthIndicator(dataSource);

        final Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertTrue(health.getDetails().get("error").toString().contains("DB error"));
    }
}
