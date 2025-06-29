package ru.katacademy.bank_app.audit.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.katacademy.bank_app.audit.annotation.Auditable;
import ru.katacademy.bank_app.audit.model.AuditEvent;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Аспект для аудита ключевых бизнес-операций, помеченных аннотацией {@link Auditable}.
 * <p>
 * Отслеживает успешное выполнение методов, формирует {@link AuditEvent}, сериализует его в JSON
 * и отправляет в Kafka через {@link AuditKafkaSender}.
 * <p>
 * Если во время сериализации или выполнения метода возникает исключение,
 * аудит либо не отправляется, либо фиксируется предупреждение.
 */
@SuppressFBWarnings(
        value = "EI_EXPOSE_REP2",
        justification = "ObjectMapper - это Spring-managed bean, безопасный для хранения ссылки"
)
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    /**
     * Сервис для отправки сообщений аудита в Kafka.
     */
    private final AuditKafkaSender auditKafkaSender;

    /**
     * Компонент для сериализации объектов в JSON.
     */
    private final ObjectMapper objectMapper;

    /**
     * Advice, выполняющийся после успешного выполнения метода, помеченного аннотацией {@link Auditable}.
     * Формирует событие аудита и отправляет его в Kafka.
     *
     * @param joinPoint текущий join point (вызов метода)
     * @param auditable аннотация {@link Auditable}, с которой был вызван метод
     * @param result    результат выполнения метода (если есть)
     */
    @AfterReturning(pointcut = "@annotation(auditable)", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Auditable auditable, Object result) {
        final AuditEvent event = buildEvent(joinPoint, auditable.action(), "SUCCESS");
        try {
            final String message = objectMapper.writeValueAsString(event);
            auditKafkaSender.send("audit-topic", message);
        } catch (JsonProcessingException e) {
            log.error("Ошибка сериализации AuditEvent для Kafka", e);
        }
    }

    /**
     * Advice, выполняющийся при выбрасывании исключения из метода, помеченного аннотацией {@link Auditable}.
     * Логирует факт пропуска аудита из-за ошибки.
     *
     * @param joinPoint текущий join point (вызов метода)
     * @param auditable аннотация {@link Auditable}, с которой был вызван метод
     * @param ex        выброшенное исключение
     */
    @AfterThrowing(pointcut = "@annotation(auditable)", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Auditable auditable, Throwable ex) {
        log.warn("Аудит пропущен из-за ошибки в действии '{}': {}", auditable.action(), ex.getMessage());
    }

    /**
     * Формирует объект {@link AuditEvent} с информацией об операции.
     *
     * @param joinPoint текущий join point (вызов метода)
     * @param action    действие, описанное в {@link Auditable#action()}
     * @param status    статус выполнения операции (например, "SUCCESS" или "FAIL")
     * @return объект события аудита
     */
    private AuditEvent buildEvent(JoinPoint joinPoint, String action, String status) {
        final String username = resolveUsernameSafely();
        final String params = Arrays.stream(joinPoint.getArgs())
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        return new AuditEvent(action, LocalDateTime.now(), username, params, status);
    }

    /**
     * Безопасно получает имя текущего пользователя из контекста безопасности Spring Security.
     * Если пользователь не аутентифицирован, возвращается строка "anonymous".
     *
     * @return имя текущего пользователя или "anonymous", если пользователь не аутентифицирован
     */
    private String resolveUsernameSafely() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "anonymous";
        }

        final Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }

        return principal.toString();
    }
}
