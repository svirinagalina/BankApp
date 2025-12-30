# 🏦 Bank-App

Мультимодульный банковский проект на Java 21 и Spring Boot. Цель системы — демонстрация полноценной микросервисной архитектуры банка: управление клиентами и счетами, безопасность и KYC, уведомления, аудит и детекция мошенничества, объединённые единой точкой входа через API Gateway.

## 📌 1. Описание проекта
Bank-App моделирует основные бизнес-процессы цифрового банка:
- создание и сопровождение счетов и пользовательских настроек;
- авторизация, безопасность и сбор статистики по аутентификации;
- верификация клиентов (KYC) и загрузка документов в хранилище;
- уведомления клиентам и аудит доменных событий;
- обнаружение мошенничества и реакция на подозрительные операции;
- проксирование запросов через API Gateway с едиными политиками.

## 📌 2. Архитектура проекта
- **Монорепозиторий Gradle**: корневой `settings.gradle` подключает все сервисы как подпроекты.
- **Мультимодульная структура**: сервисы собираются независимо, но используют общий BOM и библиотеку `bank-shared`.
- **Доменные области**: счета и настройки пользователей, безопасность и статистика логинов, KYC верификация, уведомления, аудит и противодействие мошенничеству.
- **Взаимодействие**: сервисы обмениваются событиями через Kafka и используют общие модели/утилиты из `bank-shared`. Хранилище данных — PostgreSQL, для документов используется MinIO. API Gateway проксирует публичные REST API.

## 📌 3. Используемые технологии
- **Язык и сборка**: Java 21, Gradle, собственный Bank BOM (`bank-bom`).
- **Фреймворк**: Spring Boot 3.3, Spring Cloud Gateway/Feign, Spring Web (MVC/WebFlux), Spring Security.
- **Данные**: Spring Data JPA, Flyway/Liquibase, PostgreSQL, H2 (тесты).
- **Сообщения**: Spring Kafka, Kafka + встроенный контроллер (без Zookeeper), Resilience4j + Spring Retry.
- **Хранилище файлов**: MinIO (для KYC документов).
- **Наблюдаемость**: Spring Boot Actuator, Micrometer Prometheus (auth-statistics-service).
- **Инфраструктура**: Docker/Docker Compose, MinIO, Kafka, PostgreSQL.
- **Утилиты**: Lombok, SpotBugs, Checkstyle, Jacoco, OpenAPI (springdoc).

## 📌 4. ASCII-диаграмма модулей
```
                          +-----------------+
                          |   api-gateway   |
                          +---------+-------+
                                    |
        -----------------------------------------------------------------
        |            |              |               |                  |
+---------------+ +-----------+ +---------------+ +----------------+ +--------------+
|account-service| |kyc-service| |fraud-detection| |notification-svc| |security-svc  |
+-------+-------+ +-----+-----+ +-------+-------+ +--------+-------+ +------+-------+
        |               |               |                 |                |
        |               |               |                 |                |
        |        +------+-----+   +-----+------+    +-----+------+   +-----+------+
        |        |user-settings|  |auth-statistics|  |audit        |   |bank-shared|
        |        +------+-----+   +-------------+   +------------+   +------------+
        |               |               |                 |                |
        ---------------------------------------------------------------------
                                    PostgreSQL / Kafka / MinIO
```

## 📌 5. Описание модулей
Все сервисы используют общий BOM `bank-bom` (управление версиями Spring Boot/Cloud, Jackson, Liquibase, Lombok и тестовых библиотек) и библиотеку `bank-shared` (общие модели, безопасность, Kafka/AOP утилиты).

### bank-shared
- **Назначение**: библиотека с общими сущностями, компонентами безопасности (JWT), Kafka- и AOP-утилитами, базовой конфигурацией Spring.
- **Технологии**: Spring Boot (web, security, data JPA, Kafka, AOP), JJWT, Lombok, SpotBugs.
- **Зависимости**: используется всеми сервисами; пакуется как обычный JAR (bootJar отключён).

### bank-bom
- **Назначение**: единый BOM, фиксирует версии Spring Boot/Cloud, Jackson, Lombok, Liquibase, PostgreSQL драйвера, Micrometer и Testcontainers.
- **Технологии**: Gradle Java Platform.

### api-gateway
- **Роль**: единая точка входа в систему, маршрутизация и резильентность.
- **Технологии**: Spring Cloud Gateway (WebFlux), Resilience4j, Spring Retry, Actuator.
- **Порты**: стандартно `8080` внутри контейнера (экспонирование на хост настраивается в Compose, сейчас порт не проброшен).

### account-service
- **Роль**: управление банковскими счетами и взаимодействие с аудитом.
- **Технологии**: Spring Web, Spring Data JPA, Validation, Actuator, Security, Kafka, OpenFeign, Liquibase, Springdoc, PostgreSQL.
- **Зависимости**: `bank-shared` для общих моделей и `audit` модуль для логирования действий.
- **Порт**: 8080 (в docker-compose не проброшен, используется внутри сети Compose).

### user-settings-service
- **Роль**: хранение и изменение пользовательских настроек.
- **Технологии**: Spring Web, Spring Data JPA, Spring Kafka, Actuator, Springdoc, Flyway, PostgreSQL.
- **Код-качество**: Checkstyle, SpotBugs/Jacoco правила покрывают сервисы и сущности.
- **Порт**: 8080 (проброшен на `8082` хоста в Compose).

### security-service
- **Роль**: эндпоинты безопасности (валидация токенов, вспомогательные операции для API Gateway и клиентов).
- **Технологии**: Spring Web, Actuator, Lombok, SpotBugs/Checkstyle.
- **Порт**: 8080 (в docker-compose не объявлен, предполагается внутренняя сеть).

### fraud-detection
- **Роль**: обработка событий транзакций/действий для выявления мошенничества, общается с account-service и Kafka.
- **Технологии**: Spring Web, Data JPA, AOP, Kafka, Actuator, OpenFeign, Springdoc, PostgreSQL.
- **Наблюдаемость/качество**: Checkstyle, SpotBugs, Jacoco с обязательным покрытием доменных сервисов.
- **Порт**: 8080 (проброшен на `8081` хоста в Compose).

### audit
- **Роль**: фиксация доменных событий и аудиторных записей, публикация/потребление через Kafka.
- **Технологии**: Spring Web, Data JPA, Kafka, Actuator, Flyway, PostgreSQL, SpotBugs, Checkstyle, Jacoco.
- **Порт**: 8080 (в docker-compose не проброшен).

### notification-service
- **Роль**: доставка уведомлений (email/SMS/внутренние) по событиям из Kafka и данных в БД.
- **Технологии**: Spring Web, Kafka, Actuator, Data JPA, Liquibase, PostgreSQL, Lombok, SpotBugs.
- **Порт**: 8080 (проброшен на `8083` хоста в Compose согласно Dockerfile по умолчанию).

### auth-statistics-service
- **Роль**: сбор метрик аутентификации/авторизации, публикация метрик Prometheus.
- **Технологии**: Spring Web, Data JPA, Kafka, Liquibase, Actuator, Micrometer Prometheus, Validation, PostgreSQL.
- **Зависимости**: использует `account-service` как внутреннюю библиотеку для доступа к моделям/клиентам.

### kyc-service
- **Роль**: KYC проверки и управление документами, публикует события в Kafka и хранит файлы в MinIO.
- **Технологии**: Spring Web, Data JPA, Validation, Actuator, Kafka, Liquibase, Springdoc, MinIO SDK, PostgreSQL.
- **Порт**: 8080 (проброшен на `8084` хоста).

## 📌 6. Как запускать проект
### Через Docker Compose
1. Собрать артефакты (по желанию, если хотите использовать локальные изменения):
   ```bash
   ./gradlew clean build
   ```
2. Запустить инфраструктуру и сервисы:
   ```bash
   docker compose up --build
   ```
3. Основные порты хоста по умолчанию:
    - PostgreSQL: `5434` → контейнер `5432`.
    - Kafka: `9092` (внутри) и `9094` (доступ с хоста).
    - MinIO: `9000` (API), `9001` (консоль).
    - fraud-detection: `8081`, user-settings-service: `8082`, kyc-service: `8084` (остальные сервисы доступны внутри сети `docker-compose`).


    ЗАКРЫТЫЕ ПОРТЫ, НЕ ИСПОЛЬЗОВАТЬ!
    587, 25, 2525, 389, 3389, 53413, 465



### Переменные окружения
`docker-compose.yml` задаёт основные значения (можно переопределять при запуске):
- `POSTGRES_DB=userDB`, `POSTGRES_USER=root`, `POSTGRES_PASSWORD=root`.
- `SPRING_DATASOURCE_URL/USERNAME/PASSWORD` для каждого сервиса.
- `SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092`.
- `MINIO_ENDPOINT`, `MINIO_ACCESS_KEY`, `MINIO_SECRET_KEY`, `MINIO_BUCKET` (для kyc-service).
- `SPRING_PROFILES_ACTIVE=docker` для сервисов в контейнерах.

### Возможные проблемы
- **Задержка старта БД/Kafka**: убедитесь, что healthcheck postgres прошёл перед поднятием сервисов (Compose уже использует `condition: service_healthy`).
- **Порты заняты**: измените публикацию портов в `docker-compose.yml` (например, `5435:5432`).
- **Доступ к MinIO**: при первом запуске корзина `kyc-files` создаётся через `minio-bootstrap`; если сервис падает, перезапустите `docker compose up minio minio-bootstrap`.

## 📌 7. Development Guide
- **Локальный запуск сервиса**: из каталога сервиса выполнить `./gradlew :<module>:bootRun` (БД/Kafka можно поднять через Compose). Конфигурации берутся из `application.yml`/`application-docker.yml`.
- **Сборка**: `./gradlew clean build` — соберёт все модули, применит Checkstyle/SpotBugs/Jacoco там, где подключено.
- **BOM**: зависимости задаются через `implementation platform(project(":bank-bom"))`; добавляйте новые версии в `bank-bom` вместо локальных override.
- **Кодстайл**: используйте Checkstyle конфиг `config/checkstyle/checkstyle.xml`. Запускается автоматически при `gradle check`; SpotBugs включён в большинстве сервисов.

## 📌 8. Требования к окружению
- Java 21 (JDK с поддержкой toolchain Gradle).
- Docker и Docker Compose.
- Клиент PostgreSQL (psql) для отладки схем.
- Kafka CLI (опционально) для просмотра топиков.
- Доступ к портам 5434, 9092/9094, 9000/9001 и сервисным портам из Docker Compose.

# API Gateway - Monitoring Endpoints

## 🩺 Health Checks
- **Gateway Health**: `GET /actuator/health` - Общее состояние здоровья самого API Gateway
- **Liveness Probe**: `GET /actuator/health/liveness` -  Проверка "живости" для Kubernetes (возвращает UP если приложение запущено)
- **Readiness Probe**: `GET /actuator/health/readiness` - Проверка готовности для Kubernetes (возвращает UP если приложение готово принимать трафик)

## 🔧 Gateway Monitoring
- **Gateway Routes**: `GET /actuator/gateway/routes` -  Полный список всех сконфигурированных маршрутов с фильтрами и предикатами
- **Route Filters**: `GET /actuator/gateway/routefilters` - Список доступных фильтров для маршрутов
- **Global Filters**: `GET /actuator/gateway/globalfilters` - Список глобальных фильтров, применяемых ко всем запросам

## 📊 Custom Monitoring Endpoints
- **Service Statuses**: `GET /actuator/service-status` - Текущие статусы всех downstream-сервисов (READY/NOT_READY/UNAVAILABLE)
- **Downstream Health**: `GET /actuator/downstream-health` - Cтатус всех сервисов с детальной информацией

## 📚 API Documentation
- **OpenAPI Spec**: `GET /v3/api-docs` -  OpenAPI спецификация в JSON формате
- **Swagger Config**: `GET /v3/api-docs/swagger-config` - Конфигурация Swagger UI

## 🔌 Service-specific Documentation (Документация по сервисам)
- Account Service: `/account-service/v3/api-docs` - Документация сервиса управления аккаунтами
- KYC Service: `/kyc-service/v3/api-docs` - Документация сервиса проверки клиентов
- User Settings: `/user-settings-service/v3/api-docs` - Документация сервиса настроек пользователя
- Audit Service: `/audit-service/v3/api-docs` - Документация сервиса аудита и логирования
- Security Service: `/security-service/v3/api-docs` - Документация сервиса безопасности
- Fraud Detection: `/fraud-detection/v3/api-docs` - Документация сервиса обнаружения мошенничества
- Notification Service: `/notification-service/v3/api-docs` - Документация сервиса уведомлений
- Auth Statistics: `/auth-statistics-service/v3/api-docs` - Документация сервиса статистики аутентификации

## Circuit Breaker
- Все переходы состояний логируются
- Fallback автоматически перенаправляет на соответствующие endpoint'ы

## 📈 Health Checking
- Периодическая проверка readiness: каждые 5 секунд
- Сервисы проверяются по: `{base-url}/actuator/health/readiness`
- Статусы хранятся в `DownstreamServiceRegistry`
