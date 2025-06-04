package ru.katacademy.securityservice.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * - JwtUtil - создание и валидация Jwt -токенов.
 * Использует симметричный ключ с алгоритмом HS256.
 *
 * Автор: Быстров М.
 * Дата: 03.06.2025
 */

@Component
public class JwtUtil {

    /**
     * - secretKey - Автоматически генерирующийся секретный ключ для подписи токенов
     * - expirationMs - время жизни токена 1 час.
     */
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long expirationMs = 3600000;

    /**
     * - generateToken() - Геннерирует новый токен с заданным subject (например - userName, userId)
     * @param subject - содержимаое токена
     * @return - сгенерированный токен
     */

    public String generateToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey)
                .compact();
    }

    /**
     * validateToken() - проверяет токен и возвращает claims (данные)
     * @param token - токен полученый ото клиента
     * @return - расшифрованные данные(claims) внутри токена
     * @throws JwtException - если с токен подделан, просрочен или недействителен.
     */

    public Jws<Claims> validateToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
    }
}
