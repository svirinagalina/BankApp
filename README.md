# 🏦 Bank App

Банковская система, построенная на Java 21 с использованием Spring Boot и Clean Architecture. Разрабатывается в рамках учебного проекта.

## 📚 Описание

Проект моделирует основные операции банковской системы:
- Управление пользователями и счетами
- Переводы между счетами
- Ведение истории транзакций
- Безопасность через JWT
- Построение микросервисной архитектуры (в будущем)

## 🔧 Технологии

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- Gradle
- Docker (в перспективе)
- Clean Architecture

## 🏗️ Архитектура проекта

Архитектура следует принципам Clean Architecture:
- Domain — бизнес-логика и сущности
- Application — DTO, use cases, сервисы
- Infrastructure — безопасность, репозитории, конфиги
- Presentation — контроллеры

## 📂 Модули (будущие микросервисы)

- User Service (:8083)
- Account Service ()
- Transaction Service ()
- Notification Service ()
- Audit Service ()
- KYC Service ()
- Loan Service ()
- Fraud Detection ()
- Security (SOWA-lite) (:8085)
- API Gateway ()

## Статический анализ кода
Проект использует SpotBugs для статического анализа кода.
### Запуск анализатора
Для ручного запуска:
bash:
./gradlew spotbugsMain

## Health Check Endpoints

- `/actuator/health` — общее состояние сервиса
- `/actuator/health/liveness` — сервис жив
- `/actuator/health/readiness` — сервис готов к обслуживанию


## 🧭 Архитектура микросервисов

![Архитектура микросервисов](./docs/scheme_bank.png)



🌉 API Gateway — это единая точка входа во все микросервисы банковской системы.
🔧 Возможности:
Проксирование HTTP-запросов ко всем микросервисам через централизованные маршруты
Проброс всех заголовков (в том числе Authorization)
Базовая конфигурация CORS для поддержки кросс-доменных запросов
Логирование всех проксируемых маршрутов в консоль
Лёгкое добавление новых маршрутов через application.yml
Отдельный порт (по умолчанию: 8765)



🚀 Как запустить API Gateway:
Автоматически через docker compose



⚙️ Как добавить новый маршрут:
Открой файл:
api-gateway/src/main/resources/application.yml



Добавь новый блок для своего сервиса по шаблону:
spring:
  cloud:
    gateway:
      routes:
        - id: your-service-name
          uri: http://localhost:808X
          predicates:
            - Path=/api/your-service/**
          filters:
            - StripPrefix=2
Перезапусти Gateway для применения изменений.



🔒 CORS и безопасность:
Все основные заголовки, включая Authorization, пробрасываются автоматически.


CORS открыт для всех источников по умолчанию. Для продакшена рекомендуется ограничить allowedOrigins.



## 📚 Wiki-руководства


- [Взаимодействие между слоями](https://gitlab.com/katacademy-group/banking-projects/bank-app/-/wikis/layer-module-communication)

- [Кодстайл](https://gitlab.com/katacademy-group/banking-projects/bank-app/-/wikis/code-style-guide)

- [Подготовка к клонированию репозитория](https://gitlab.com/katacademy-group/banking-projects/bank-app/-/wikis/setup-before-cloning)

и другие руководства

## 🚀 Как запустить

```bash
/gradlew clean build
docker compose build
docker compose up
