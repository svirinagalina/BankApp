package ru.katacademy.securityservice.presentation.controller;
import io.jsonwebtoken.JwtException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.katacademy.securityservice.util.JwtUtil;
import java.util.Map;

/**
 * JwtController — REST-контроллер для проверки валидности JWT-токенов.
 *
 *  - Принимает POST-запрос с JWT-токеном в теле;
 *  - Проверяет подпись и срок действия токена через JwtUtil;
 *  - Возвращает HTTP 200 с расшифрованными claim при успехе;
 *  - Возвращает HTTP 401 при недействительном токене.
 *
 * Пример использования:
 *  POST /api/security/verify
 *  Тело: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
 *
 * Ответ: { "sub": "user", "iat": ..., "exp": ... } или "Invalid token..."
 *
 * Зависимости:
 *  - JwtUtil: компонент для создания/разбора JWT-токенов.
 *
 * Автор: Быстров М.
 * Дата: 10.06.2025
 */
@RestController
@RequestMapping("/api/security")
public class JwtController {

    private final JwtUtil jwtUtil;

    /**
     * Конструктор с внедрением зависимости JwtUtil
     *
     * @param jwtUtil компонент, содержащий логику работы с JWT
     */
    public JwtController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * verifyToken — точка входа для проверки JWT-токена.
     *
     * HTTP-метод: POST
     * Путь запроса: /api/security/verify
     * Ожидает: строку токена в теле запроса (тип: application/json)
     *
     * @param token JWT-токен в виде строки.
     * @return 200 OK и claims (Map<String, Object>) — если токен действителен
     *         401 Unauthorized и сообщение — если токен некорректен или просрочен
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestBody String token) {
        try {
            final Map<String, Object> claims = jwtUtil.getClaimsCopy(token);
            return ResponseEntity.ok(claims);
        } catch (JwtException e) {
            return ResponseEntity.status(401).body("Invalid token" + e.getMessage());
        }
    }
}
