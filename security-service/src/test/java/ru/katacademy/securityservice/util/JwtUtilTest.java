package ru.katacademy.securityservice.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для {@link JwtUtil} - утилиты для работы с JWT токенами.
 * Проверяет корректность генерации и верификации JWT токенов.
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private static final String testUser = "testUser";

    /**
     * Инициализация перед каждым тестом:
     * 1. Создает новый экземпляр JwtUtil
     * 2. Устанавливает тестовые значения для secret и expiration
     */
    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "testsecretkeyfortestpurposesonly1234567890");
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", 3600000L);
    }

    /**
     * Тест генерации токена.
     * Проверяет что:
     * 1. Токен не null
     * 2. Имеет правильную структуру (3 части, разделенные точками)
     */
    @Test
    void generateToken_ShouldReturnValidToken() {
        final String token = jwtUtil.generateToken(
                "testUser",
                123L,
                List.of("ROLE_USER")
        );

        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);
    }

    /**
     * Тест извлечения username из токена.
     * Проверяет что извлеченное имя пользователя соответствует исходному.
     */
    @Test
    void getSubject_ShouldReturnCorrectUsername() {
        final String token = jwtUtil.generateToken(
                "testUser",
                123L,
                List.of("ROLE_USER"));
        final String subject = jwtUtil.getSubject(token);

        assertEquals(testUser, subject);
    }

    /**
     * Тест извлечения claims (утверждений) из токена.
     * Проверяет наличие обязательных полей:
     * - sub (subject)
     * - iat (issued at)
     * - exp (expiration)
     * и соответствие subject исходному имени пользователя.
     */
    @Test
    void getClaimsCopy_ShouldContainRequiredFields() {
        final String token = jwtUtil.generateToken(
                "testUser",
                123L,
                List.of("ROLE_USER"));
        final Map<String, Object> claims = jwtUtil.getClaimsCopy(token);

        assertNotNull(claims);
        assertTrue(claims.containsKey("sub"));
        assertTrue(claims.containsKey("iat"));
        assertTrue(claims.containsKey("exp"));
        assertEquals(testUser, claims.get("sub"));
    }

    /**
     * Тест обработки невалидного токена.
     * Проверяет что при передаче некорректного токена выбрасывается исключение.
     */
    @Test
    void getSubject_ShouldThrowExceptionForInvalidToken() {
        assertThrows(Exception.class,
                () -> jwtUtil.getSubject("invalid.token.here"));
    }
}