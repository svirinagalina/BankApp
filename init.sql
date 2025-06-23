-- Создаём базу под notification-service
CREATE DATABASE notifications_db;

GRANT ALL PRIVILEGES ON SCHEMA public TO root;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO root;
