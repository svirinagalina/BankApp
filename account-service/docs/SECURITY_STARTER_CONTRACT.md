# Security Starter - Контракт использования

## Обзор

Этот документ описывает контракт взаимодействия между **account-service** и **security-starter** библиотекой.

**security-starter** — это переиспользуемая инфраструктурная библиотека, которая:
- Не знает о доменной модели сервиса
- Предоставляет минимальный механизм аутентификации через Spring Security
- Использует инверсию зависимостей через extension point `UserIdentityResolver`

**account-service** — reference-сервис, демонстрирующий корректное использование starter'а.

---

## 🎯 Ключевая идея

```
👉 security-starter НЕ ЗНАЕТ, кто такой пользователь
👉 account-service САМ описывает, как получить пользователя из запроса
```

Взаимодействие строится **исключительно через контракт**, без протаскивания доменных сущностей в starter.

---

## 📜 Контракт

### Интерфейс `UserIdentityResolver`

```java
public interface UserIdentityResolver {
    /**
     * Извлекает идентификатор пользователя из HTTP-запроса.
     *
     * @param request HTTP-запрос
     * @return идентификатор пользователя или null, если не найден
     */
    Long resolve(HttpServletRequest request);
}
```

### Класс `UserPrincipal`

```java
public record UserPrincipal(Long userId) {
}
```

---

## 🔧 Обязанности сторон

### Обязанности **security-starter**

security-starter отвечает за:

1. **Автоматическую конфигурацию Spring Security**
   - Регистрация `SecurityFilterChain`
   - Отключение CSRF (для MVP)
   - Разрешение всех запросов (авторизация на уровне бизнес-логики)

2. **Интеграцию `AuthenticationFilter`**
   - Перехват HTTP-запросов
   - Вызов `UserIdentityResolver.resolve(request)`
   - Создание `UserPrincipal` на основе userId
   - Создание `Authentication` и размещение в `SecurityContext`

3. **Предоставление контракта**
   - Интерфейс `UserIdentityResolver` для реализации сервисом
   - Класс `UserPrincipal` для использования в бизнес-логике
   - Класс `UserAuthentication` (внутренняя реализация `Authentication`)

4. **Условную активацию**
   - Starter активируется только при наличии Spring Security в classpath
   - Starter НЕ активируется, если сервис уже определил свой `SecurityFilterChain`

**Что НЕ делает security-starter:**
- ❌ Не знает о доменных сущностях (User, Account, etc.)
- ❌ Не реализует JWT / OAuth / Session-based аутентификацию
- ❌ Не управляет UserDetailsService
- ❌ Не выполняет авторизацию (разрешения, роли)
- ❌ Не требует обязательной аутентификации (всё через бизнес-логику)

---

### Обязанности **account-service**

account-service отвечает за:

1. **Реализацию `UserIdentityResolver`**
   - Извлечение информации о пользователе из HTTP-запроса
   - Валидация и парсинг данных
   - Возврат `userId` или `null`

2. **Определение стратегии аутентификации**
   - MVP: извлечение из заголовка `X-User-Id`
   - В будущем: JWT, OAuth2, Session, etc.
   - Starter не знает и не должен знать о деталях реализации

3. **Использование `UserPrincipal` в бизнес-логике**
   - Получение аутентифицированного пользователя из `SecurityContext`
   - Валидация прав доступа на уровне сервисов
   - Передача userId в доменную логику

4. **Подключение dependency**
   ```gradle
   implementation project(':security-starter')
   ```

**Что НЕ делает account-service:**
- ❌ Не создаёт собственный `SecurityFilterChain` (использует из starter'а)
- ❌ Не зависит от внутренней реализации starter'а
- ❌ Не передаёт доменные сущности в starter

---

## 🚀 Условия активации security-starter

security-starter автоматически активируется при соблюдении условий:

1. ✅ Spring Security присутствует в classpath
2. ✅ Это web-приложение (servlet-based)
3. ✅ Сервис **НЕ** определил собственный `SecurityFilterChain`

Если сервис хочет полностью переопределить конфигурацию безопасности, он может создать свой `SecurityFilterChain` bean, и starter не будет активирован.

---

## 📋 Примеры использования

### 1. Реализация UserIdentityResolver в сервисе

```java
@Component
@Slf4j
public class AccountUserIdentityResolver implements UserIdentityResolver {

    private static final String USER_ID_HEADER = "X-User-Id";

    @Override
    public Long resolve(HttpServletRequest request) {
        final String userIdHeader = request.getHeader(USER_ID_HEADER);

        if (userIdHeader == null || userIdHeader.isBlank()) {
            log.debug("X-User-Id header missing");
            return null;
        }

        try {
            return Long.parseLong(userIdHeader);
        } catch (NumberFormatException e) {
            log.warn("Invalid X-User-Id header: {}", userIdHeader);
            return null;
        }
    }
}
```

### 2. Использование UserPrincipal в контроллере

```java
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        final Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401)
                .body(Map.of("error", "Not authenticated"));
        }

        if (!(authentication.getPrincipal() instanceof UserPrincipal userPrincipal)) {
            return ResponseEntity.status(401)
                .body(Map.of("error", "Invalid principal"));
        }

        // Используем userId в бизнес-логике
        return ResponseEntity.ok(Map.of(
            "userId", userPrincipal.userId(),
            "authenticated", true
        ));
    }
}
```

### 3. Использование UserPrincipal в сервисе

```java
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Account getCurrentUserAccount() {
        final Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        final UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        final Long userId = principal.userId();

        return accountRepository.findByUserId(userId)
            .orElseThrow(() -> new AccountNotFoundException(userId));
    }
}
```

---

## 🔄 Эволюция аутентификации

Благодаря инверсии зависимостей через `UserIdentityResolver`, сервис может менять стратегию аутентификации **без изменения security-starter**.

### Пример: Переход на JWT

```java
@Component
@Slf4j
public class JwtUserIdentityResolver implements UserIdentityResolver {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Long resolve(HttpServletRequest request) {
        final String token = extractToken(request);

        if (token == null) {
            return null;
        }

        try {
            return jwtTokenProvider.getUserIdFromToken(token);
        } catch (JwtException e) {
            log.warn("Invalid JWT token", e);
            return null;
        }
    }

    private String extractToken(HttpServletRequest request) {
        final String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

**Изменения требуются только в account-service. security-starter остаётся неизменным.**

---

## 🧪 Тестирование

### Unit-тесты для UserIdentityResolver

```java
@DisplayName("AccountUserIdentityResolver Tests")
class AccountUserIdentityResolverTest {

    private final AccountUserIdentityResolver resolver =
        new AccountUserIdentityResolver();

    @Test
    void shouldResolveUserIdFromValidHeader() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-User-Id", "12345");

        final Long userId = resolver.resolve(request);

        assertThat(userId).isEqualTo(12345L);
    }

    @Test
    void shouldReturnNullWhenHeaderMissing() {
        final MockHttpServletRequest request = new MockHttpServletRequest();

        final Long userId = resolver.resolve(request);

        assertThat(userId).isNull();
    }
}
```

### Интеграционные тесты

```java
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Security Starter Integration Tests")
class SecurityStarterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldPopulateSecurityContextWithValidHeader() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/me")
                .header("X-User-Id", "12345"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(12345))
            .andExpect(jsonPath("$.authenticated").value(true));
    }

    @Test
    void shouldReturn401WhenHeaderMissing() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/me"))
            .andExpect(status().isUnauthorized());
    }
}
```

---

## 📊 Диаграмма взаимодействия

```
┌─────────────────────────────────────────────────────────────────┐
│                         HTTP Request                             │
│                     (X-User-Id: 12345)                           │
└─────────────────────┬───────────────────────────────────────────┘
                      │
                      ▼
         ┌────────────────────────────┐
         │  AuthenticationFilter      │ ◄─── security-starter
         │  (security-starter)        │
         └────────────┬───────────────┘
                      │
                      │ resolve(request)
                      ▼
         ┌────────────────────────────┐
         │ AccountUserIdentityResolver│ ◄─── account-service
         │    (account-service)       │      (реализует интерфейс)
         └────────────┬───────────────┘
                      │
                      │ return userId: 12345
                      ▼
         ┌────────────────────────────┐
         │  AuthenticationFilter      │
         │  создаёт UserPrincipal     │ ◄─── security-starter
         │  и устанавливает в         │
         │  SecurityContext           │
         └────────────┬───────────────┘
                      │
                      ▼
         ┌────────────────────────────┐
         │    Controller / Service    │ ◄─── account-service
         │  получает UserPrincipal    │      (использует userId)
         │  из SecurityContext        │
         └────────────────────────────┘
```

---

## ✅ Контрольный чек-лист

### Для сервиса (account-service):

- [x] Подключена зависимость на security-starter
- [x] Реализован `UserIdentityResolver` с аннотацией `@Component`
- [x] Resolver извлекает userId из HTTP-запроса
- [x] Resolver не зависит от доменных сущностей
- [x] Контроллеры получают `UserPrincipal` из `SecurityContext`
- [x] Написаны unit-тесты для `UserIdentityResolver`
- [x] Написаны интеграционные тесты для проверки SecurityContext

### Для starter'а (security-starter):

- [x] Определён интерфейс `UserIdentityResolver`
- [x] Определён класс `UserPrincipal`
- [x] Реализован `AuthenticationFilter`
- [x] Настроена автоконфигурация через `@AutoConfiguration`
- [x] Условная активация через `@ConditionalOnMissingBean`
- [x] Starter НЕ зависит от доменных сущностей

---

## 🎓 Архитектурные принципы

1. **Инверсия зависимостей** — starter зависит от абстракции (`UserIdentityResolver`), сервис предоставляет реализацию
2. **Разделение ответственности** — starter управляет Spring Security, сервис управляет логикой аутентификации
3. **Открыт для расширения, закрыт для модификации** — можно менять стратегию аутентификации без изменения starter'а
4. **Не утекают детали реализации** — starter не знает о JWT, headers, sessions

---

## 📚 Дополнительные ресурсы

- [Spring Boot Auto-Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.developing-auto-configuration)
- [Spring Security Architecture](https://spring.io/guides/topicals/spring-security-architecture)
- [Strategy Pattern](https://refactoring.guru/design-patterns/strategy)
- [Dependency Inversion Principle](https://en.wikipedia.org/wiki/Dependency_inversion_principle)

---

**Версия:** 1.0
**Дата:** 2026-01-29
**Автор:** Galina Svirina
