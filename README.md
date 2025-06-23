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

## 📚 Wiki-руководства


- [Взаимодействие между слоями](https://gitlab.com/katacademy-group/banking-projects/bank-app/-/wikis/layer-module-communication)

- [Кодстайл](https://gitlab.com/katacademy-group/banking-projects/bank-app/-/wikis/code-style-guide)

- [Подготовка к клонированию репозитория](https://gitlab.com/katacademy-group/banking-projects/bank-app/-/wikis/setup-before-cloning)

и другие руководства

## 🚀 Как запустить

```bash
./gradlew bootRun
