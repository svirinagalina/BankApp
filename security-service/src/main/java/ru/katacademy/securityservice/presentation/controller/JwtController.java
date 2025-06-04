package ru.katacademy.securityservice.presentation.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.katacademy.securityservice.util.JwtUtil;

/**
 * - JwtController - контроллер через REST c токенами.
 * - энд поинты для валидации токенов.
 */
@RestController
@RequestMapping("/api/security")
public class JwtController {

    private final JwtUtil jwtUtil;

    public JwtController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * - verifyToken() - проверяет токен отправленный в теле POST-запроса
     * @param token - строка токена
     * @return - статус 200 ок, с расшифрованными данными или 401 если токен некорректен
     */

    @PostMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestBody String token) {
        try {
            Jws<Claims> claims = jwtUtil.validateToken(token);
            return ResponseEntity.ok(claims.getBody());
        } catch (JwtException e) {
            return ResponseEntity.status(401).body("Invalid token" + e.getMessage());
        }
    }
}
