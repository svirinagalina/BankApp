# Security Starter — Контракт идентификации пользователя

> **Версия:** 1.0  
> **Дата:** 2026-02-02  
> **Автор:** Galina Svirina  
> **Статус:** Accepted

---

## 1. Цель документа

Зафиксировать единый контракт пользовательской идентификации между `security-starter` и сервисами, которые его подключают.

Документ отвечает на вопрос: **«Как любой сервис должен интегрироваться с security-starter?»**

---

## 2. Архитектурное решение (ADR)

### Контекст

В системе несколько сервисов, каждый из которых нуждается в идентификации пользователя. Идентификация должна быть единообразной, но при этом:

- не привязанной к конкретному транспорту (HTTP-заголовки, JWT, cookie);
- не привязанной к доменной модели конкретного сервиса;
- расширяемой без модификации библиотеки.

### Решение

Ввести двухуровневую модель:

1. **`UserIdentity`** — транспортно-независимый контракт, описывающий «кто такой пользователь» в контексте системы. Это модель уровня архитектуры.
2. **`UserIdentityResolver`** — точка расширения (extension point), через которую конкретный сервис реализует извлечение `UserIdentity` из запроса. Это инфраструктурный интерфейс.

### Альтернативы, которые были отклонены

| Альтернатива | Причина отклонения |
|---|---|
| Передавать `userId` напрямую через заголовок без контракта | Неявная зависимость; любое изменение формата ломает потребителей |
| Использовать `UserDetailsService` из Spring Security | Привязка к Spring Security; невозможно использовать вне Spring |
| Встроить JWT-парсинг в starter | Привязка к конкретному механизму аутентификации; нарушает OCP |
| Передавать доменную модель `User` через starter | Starter начинает зависеть от доменных сущностей сервиса |

### Последствия

- Каждый новый сервис обязан реализовать `UserIdentityResolver`.
- Starter остаётся независимой библиотекой без доменных зависимостей.
- Смена механизма аутентификации требует изменений только в реализации резолвера конкретного сервиса.

---

## 3. Модель идентификации: `UserIdentity`

### Определение

```java
/**
 * Транспортно-независимая модель идентификации пользователя.
 * Описывает минимальный набор данных, которые security-starter
 * предоставляет сервисам через SecurityContext.
 *
 * Не зависит от HTTP, JWT, Spring Security или доменной модели сервиса.
 */
public record UserIdentity(
    Long userId,
    Set<String> roles,
    Map<String, Object> attributes
) {
    /**
     * Минимальный конструктор — только обязательное поле.
     */
    public UserIdentity(Long userId) {
        this(userId, Set.of(), Map.of());
    }
}
```

### Поля контракта

| Поле | Тип | Обязательность | Назначение |
|---|---|---|---|
| `userId` | `Long` | **Обязательное** | Уникальный идентификатор пользователя в системе |
| `roles` | `Set<String>` | Опциональное | Роли пользователя (пустой `Set` по умолчанию) |
| `attributes` | `Map<String, Object>` | Опциональное | Расширяемые метаданные (пустая `Map` по умолчанию) |

### Правила

- `userId` не может быть `null` в успешно разрешённой идентичности.
- `roles` и `attributes` никогда не `null` — всегда пустая коллекция по умолчанию.
- Starter оперирует только `UserIdentity`. Он **не знает**, откуда взялись данные.
- Сервис **не обязан** заполнять `roles` и `attributes` — минимальный контракт требует только `userId`.

---

## 4. Extension point: `UserIdentityResolver`

### Определение

```java
/**
 * Точка расширения для сервисов.
 * Сервис реализует этот интерфейс, чтобы описать,
 * КАК извлечь UserIdentity из входящего запроса.
 *
 * Реализация регистрируется как Spring-компонент.
 */
public interface UserIdentityResolver {

    /**
     * Извлекает идентичность пользователя из HTTP-запроса.
     *
     * @param request входящий HTTP-запрос
     * @return UserIdentity или null, если пользователь не идентифицирован
     */
    UserIdentity resolve(HttpServletRequest request);
}
```

### Почему `HttpServletRequest` в сигнатуре

`UserIdentityResolver` — это **инфраструктурный** интерфейс, а не доменный. Он существует на границе между транспортом и доменом. Его задача — преобразовать транспортные данные (запрос) в доменно-независимый контракт (`UserIdentity`).

```
HTTP Request  →  [UserIdentityResolver]  →  UserIdentity  →  SecurityContext  →  Бизнес-логика
                  ↑ инфраструктура            ↑ контракт                          ↑ домен
```

Бизнес-логика сервиса работает только с `UserIdentity` и никогда не видит `HttpServletRequest`.

---

## 5. Границы ответственности

### Что делает `security-starter`

| Ответственность | Описание |
|---|---|
| Предоставляет контракт | `UserIdentity`, `UserIdentityResolver` |
| Конфигурирует Spring Security | `SecurityFilterChain`, отключение CSRF (MVP) |
| Запускает фильтр аутентификации | `AuthenticationFilter` вызывает `resolve()` на каждый запрос |
| Размещает результат в SecurityContext | Создаёт `Authentication` на основе `UserIdentity` |
| Активируется условно | Только при наличии Spring Security и отсутствии кастомного `SecurityFilterChain` |

### Что НЕ делает `security-starter`

- Не знает о доменных сущностях (User, Account, Transaction и т.д.)
- Не реализует конкретный механизм аутентификации (JWT, OAuth, session)
- Не выполняет авторизацию (проверку прав, ролей)
- Не управляет `UserDetailsService`

### Что делает прикладной сервис

| Ответственность | Описание |
|---|---|
| Реализует `UserIdentityResolver` | Определяет, КАК извлечь `UserIdentity` из запроса |
| Выбирает стратегию аутентификации | Header, JWT, OAuth — детали скрыты внутри резолвера |
| Использует `UserIdentity` в бизнес-логике | Получает из `SecurityContext`, работает с `userId`, `roles` |
| Подключает зависимость | Добавляет `security-starter` в `build.gradle` |

### Что НЕ делает прикладной сервис

- Не создаёт собственный `SecurityFilterChain` (если хочет использовать starter)
- Не передаёт доменные сущности в starter
- Не зависит от внутренней реализации starter

### Диаграмма

```
┌─────────────────────────────────────────────────────────────┐
│                      HTTP Request                           │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
              ┌────────────────────────┐
              │  AuthenticationFilter  │  ◄── security-starter
              └────────────┬───────────┘
                           │ resolve(request)
                           ▼
              ┌────────────────────────┐
              │ UserIdentityResolver   │  ◄── прикладной сервис
              │ (реализация сервиса)   │      (реализует интерфейс)
              └────────────┬───────────┘
                           │ return UserIdentity(userId, roles, attributes)
                           ▼
              ┌────────────────────────┐
              │  AuthenticationFilter  │
              │  создаёт Authentication│  ◄── security-starter
              │  из UserIdentity       │
              │  → SecurityContext     │
              └────────────┬───────────┘
                           │
                           ▼
              ┌────────────────────────┐
              │  Controller / Service  │  ◄── прикладной сервис
              │  работает с            │
              │  UserIdentity          │
              └────────────────────────┘
```

---

## 6. Инструкция по интеграции

### Шаг 1. Подключить зависимость

```gradle
implementation project(':security-starter')
```

### Шаг 2. Реализовать `UserIdentityResolver`

Создать `@Component`, который имплементирует `UserIdentityResolver` и извлекает `UserIdentity` из запроса. Способ извлечения определяет сервис.

**Пример: извлечение из заголовка (MVP)**

```java
@Component
@Slf4j
public class HeaderUserIdentityResolver implements UserIdentityResolver {

    private static final String USER_ID_HEADER = "X-User-Id";

    @Override
    public UserIdentity resolve(HttpServletRequest request) {
        String header = request.getHeader(USER_ID_HEADER);

        if (header == null || header.isBlank()) {
            log.debug("X-User-Id header is missing");
            return null;
        }

        try {
            Long userId = Long.parseLong(header);
            return new UserIdentity(userId);
        } catch (NumberFormatException e) {
            log.warn("Invalid X-User-Id header value: {}", header);
            return null;
        }
    }
}
```

**Пример: извлечение из JWT (будущее)**

```java
@Component
@Slf4j
public class JwtUserIdentityResolver implements UserIdentityResolver {

    private final JwtTokenProvider jwtProvider;

    @Override
    public UserIdentity resolve(HttpServletRequest request) {
        String token = extractBearerToken(request);
        if (token == null) return null;

        try {
            Long userId = jwtProvider.getUserId(token);
            Set<String> roles = jwtProvider.getRoles(token);
            return new UserIdentity(userId, roles, Map.of());
        } catch (JwtException e) {
            log.warn("Invalid JWT token", e);
            return null;
        }
    }

    private String extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
```

### Шаг 3. Использовать `UserIdentity` в бизнес-логике

```java
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof UserIdentity identity)) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        return ResponseEntity.ok(Map.of(
            "userId", identity.userId(),
            "authenticated", true
        ));
    }
}
```

### Шаг 4. Убедиться, что starter активирован

Starter активируется автоматически при выполнении всех условий:

1. Spring Security в classpath.
2. Servlet-based web-приложение.
3. Сервис **не** определяет собственный `SecurityFilterChain`.

Если сервис определяет свой `SecurityFilterChain`, starter не активируется.

---

## 7. Готовность к масштабированию

### Другой способ аутентификации

Сервис заменяет реализацию `UserIdentityResolver`. Starter не меняется.

### Другой сервис подключает starter

Новый сервис выполняет четыре шага из раздела 6. Никакой координации с другими сервисами не требуется.

### Несколько реализаций резолвера

Если в будущем потребуется выбор между резолверами (например, header для внутренних вызовов и JWT для внешних), сервис может использовать `@Primary`, `@Conditional*`-аннотации или создать composable-резолвер, делегирующий конкретным реализациям. Starter по-прежнему вызывает единственный `UserIdentityResolver` из контекста.

### Расширение модели идентичности

Поле `attributes` в `UserIdentity` позволяет передавать произвольные метаданные (tenant ID, email, locale) без изменения контракта.

---

## 8. Ограничения

- Документ фиксирует контракт **на текущем этапе (MVP)**.
- Авторизация (роли, разрешения) находится за пределами scope этого контракта.
- Starter не поддерживает reactive stack (`WebFlux`).
- Контракт предполагает single-tenant по умолчанию; multi-tenancy реализуется через `attributes`.

---

## 9. Глоссарий

| Термин | Определение |
|---|---|
| **UserIdentity** | Транспортно-независимая модель, описывающая идентифицированного пользователя |
| **UserIdentityResolver** | Extension point; интерфейс, через который сервис реализует логику извлечения `UserIdentity` из запроса |
| **security-starter** | Переиспользуемая инфраструктурная библиотека для настройки Spring Security |
| **Прикладной сервис** | Любой сервис, подключающий security-starter (например, account-service) |
| **Контракт** | Формальное соглашение между starter и сервисом о формате обмена данными |
