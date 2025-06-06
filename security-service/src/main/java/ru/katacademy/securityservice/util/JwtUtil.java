//package ru.katacademy.securityservice.util;
//
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.nio.charset.StandardCharsets;
//import java.security.Key;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * - JwtUtil - создание и валидация Jwt-токенов.
// * Использует симметричный ключ с алгоритмом HS256.
// *
// * Автор: Быстров М.
// * Дата: 05.06.2025
// */
//
//@Component
//public class JwtUtil {
//
//    @Value("${jwt.secret}")
//    private String secret;
//
//    @Value("${jwt.expiration-ms:3600000}")
//    private long expirationMs;
//
//    private Key getSigningKey() {
//        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
//    }
//
//
//    public String generateToken(String subject) {
//        return Jwts.builder()
//                .setSubject(subject)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
//                .signWith(getSigningKey())
//                .compact();
//    }
//
//    /**
//     * Валидирует токен и возвращает все claims.
//     *
//     * @param token токен от клиента
//     * @return копия claims
//     * @throws JwtException если токен просрочен, недействителен или подделан
//     */
//    public Map<String, Object> getClaimsCopy(String token) {
//        final Claims claims = parseToken(token).getBody();
//        return new HashMap<>(claims);
//    }
//
//    /**
//     * Извлекает subject из токена.
//     *
//     * @param token токен от клиента
//     * @return subject (например, имя пользователя)
//     * @throws JwtException если токен недействителен
//     */
//    public String getSubject(String token) {
//        return parseToken(token).getBody().getSubject();
//
//    }
//
//    private Jws<Claims> parseToken(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(getSigningKey())
//                .build()
//                .parseClaimsJws(token);
//    }
//}
