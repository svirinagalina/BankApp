# 📦 Domain Models + DTO — структура и назначения

## 👤 User (domain.entity.User)

+-----------------------------+
|         User                |
+-----------------------------+
| Long id                    | // Уникальный идентификатор пользователя
| FullName fullName          | // Полное имя пользователя
| Email email                | // Email в виде Value Object с валидацией
| String passwordHash        | // Захешированный пароль
| UserRole role              | // Роль: USER, ADMIN (enum)
| LocalDateTime createdAt    | // Дата и время регистрации
+-----------------------------+

## 💳 Account (domain.entity.Account)

+------------------------------+
|         Account              |
+------------------------------+
| Long id                     | // Уникальный ID счёта
| AccountNumber accountNumber | // VO: валидный номер счёта (20 цифр)
| Long userId                 | // ID владельца счёта (User)
| Money balance               | // VO: сумма и валюта
| Currency currency           | // Валюта счёта (новая VO, ниже)
| AccountStatus status        | // Enum: ACTIVE, BLOCKED, CLOSED
| LocalDateTime createdAt     | // Когда счёт был открыт
+------------------------------+

## 📧 Email (domain.valueobject.Email)

+-------------------------+
|        Email            |
+-------------------------+
| Email(String email)     | // Валидация на этапе создания
| String value()          |
| boolean isValid()       |
+-------------------------+

## 💰 Money (domain.valueobject.Money)

+-------------------------------+
|           Money               |
+-------------------------------+
| Money(BigDecimal, Currency)  | // Содержит сумму и валюту
| BigDecimal amount()          | // Сама сумма
| Currency currency()          | // Валюта (см. ниже)
| Money add(Money)             | // Операции
| Money subtract(Money)        |
+-------------------------------+

## 💱 Currency (domain.valueobject.Currency)

+-----------------------------+
|         Currency            |
+-----------------------------+
| String code                 | // ISO-код валюты (RUB, USD, EUR)
| String name                 | // Название валюты
| int scale                   | // Кол-во знаков после запятой (например 2)
+-----------------------------+

## 🏦 AccountNumber (domain.valueobject.AccountNumber)

+-------------------------------+
|       AccountNumber           |
+-------------------------------+
| AccountNumber(String)        | // Валидация на 20 цифр
| String value()               |
| boolean isValid()            |
+-------------------------------+

## 📤 DTO: UserDto

+-----------------------------+
|       UserDto              |
+-----------------------------+
| Long id                    | // Идентификатор
| String fullName            | // Полное имя
| String email               | // Email строкой
| String role                | // USER / ADMIN
+-----------------------------+

## 📥 DTO: RegisterUserCommand

+-----------------------------+
| RegisterUserCommand         |
+-----------------------------+
| String fullName             | // Имя при регистрации
| String email                | // Email
| String password             | // Открытый пароль (будет захеширован)
+-----------------------------+
