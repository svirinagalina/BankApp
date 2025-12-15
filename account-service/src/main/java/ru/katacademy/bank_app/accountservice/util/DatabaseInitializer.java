package ru.katacademy.bank_app.accountservice.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Автор: Aleksandr Bronnikov
 * <br>
 * Дата: 15.12.2025
 *
 * <p>Автоматическое создание базы данных при запуске сервиса</p>
 */

public class DatabaseInitializer {

    public static void main(String[] args) {
        if (args.length != 5) {
            System.err.println("Использование: <Base_URL> <User> <Password> <Target_DB_Name> <Target_Schema_Name>");
            System.exit(1);
        }

        String baseDbUrl = args[0];       // jdbc:postgresql://localhost:5432/postgres
        String user = args[1];            // root
        String pass = args[2];            // root
        String targetDb = args[3];        // mainDB
        String targetSchema = args[4];    // account-service_local

        // 1. Создание целевой БД (mainDB)
        if (createDatabase(baseDbUrl, user, pass, targetDb)) {
            // 2. Создание целевой схемы (account-service_local)
            String targetDbUrl = "jdbc:postgresql://localhost:5432/" + targetDb;
            createSchema(targetDbUrl, user, pass, targetSchema);
        }
    }

    private static boolean createDatabase(String baseDbUrl, String user, String pass, String targetDb) {
        System.out.println("Проверка и создание БД: " + targetDb);
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(baseDbUrl, user, pass);
            Statement stmt = conn.createStatement();

            String checkSql = "SELECT 1 FROM pg_database WHERE datname = '" + targetDb + "'";
            ResultSet rs = stmt.executeQuery(checkSql);

            if (!rs.next()) {
                System.out.println("БД '" + targetDb + "' не найдена. Создаю...");
                String createSql = "CREATE DATABASE \"" + targetDb + "\" WITH OWNER = " + user;
                stmt.executeUpdate(createSql);
                System.out.println("БД '" + targetDb + "' успешно создана.");
            } else {
                System.out.println("БД '" + targetDb + "' уже существует. Пропускаю.");
            }
            return true;
        } catch (Exception e) {
            System.err.println("ОШИБКА создания БД. Убедитесь, что PostgreSQL запущен и доступен по URL: " + baseDbUrl);
            System.out.println(e.getMessage());
            return false;
        } finally {
            closeConnection(conn);
        }
    }

    private static void createSchema(String targetDbUrl, String user, String pass, String targetSchema) {
        System.out.println("Проверка и создание схемы: " + targetSchema);
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(targetDbUrl, user, pass);
            Statement stmt = conn.createStatement();

            // CREATE SCHEMA IF NOT EXISTS - самая безопасная команда
            String createSchemaSql = "CREATE SCHEMA IF NOT EXISTS \"" + targetSchema + "\" AUTHORIZATION " + user;
            stmt.executeUpdate(createSchemaSql);
            System.out.println("Схема '" + targetSchema + "' готова.");

        } catch (Exception e) {
            System.err.println("ОШИБКА создания схемы. Проверьте права пользователя: " + user);
            System.out.println(e.getMessage());
        } finally {
            closeConnection(conn);
        }
    }

    private static void closeConnection(Connection conn) {
        try {
            if (conn != null) conn.close();
        } catch (Exception ignored) {
        }
    }
}