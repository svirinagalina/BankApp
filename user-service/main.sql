-- создаём базу, если ещё нет (опционально)
CREATE DATABASE userDB;
/connect userDB

-- даём все привилегии пользователю root на схему public
GRANT ALL PRIVILEGES ON SCHEMA public TO root;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO root;
