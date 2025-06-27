package ru.katacademy.bank_app.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.katacademy.bank_app.audit.annotation.Auditable;
import ru.katacademy.bank_app.audit.aspect.AuditAspect;
import ru.katacademy.bank_app.audit.aspect.AuditKafkaSender;


import static org.mockito.Mockito.*;

/**
 * Юнит-тесты для {@link AuditAspect}, проверяющие его поведение
 * при перехвате методов, аннотированных {@link Auditable}.
 */
class AuditAspectUnitTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private AuditKafkaSender auditKafkaSender;

    @InjectMocks
    private AuditAspect auditAspect;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    @Mock
    private Auditable auditable;

    /**
     * Инициализация моков и базовых заглушек перед каждым тестом.
     * Настраиваются аргументы метода, подпись и значение аннотации {@link Auditable}.
     */
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        when(joinPoint.getArgs()).thenReturn(new Object[]{"param1", 123});
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getParameterNames()).thenReturn(new String[]{"arg1", "arg2"});
        when(auditable.action()).thenReturn("TestAction");
    }

    /**
     * Проверяет, что при успешном завершении метода создаётся событие аудита,
     * сериализуется и отправляется через {@link AuditKafkaSender}.
     */
    @Test
    void testAfterReturning_Success() throws Exception {
        final String json = "{\"action\":\"TestAction\"}";

        when(objectMapper.writeValueAsString(any())).thenReturn(json);

        auditAspect.afterReturning(joinPoint, auditable, "result");

        verify(auditKafkaSender, times(1)).send("audit-topic", json);
    }

    /**
     * Проверяет, что если при сериализации возникает ошибка,
     * событие не отправляется в Kafka.
     */
    @Test
    void testAfterReturning_SerializationError() throws Exception {
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("fail") {});

        auditAspect.afterReturning(joinPoint, auditable, "result");

        verify(auditKafkaSender, never()).send(anyString(), anyString());
    }

    /**
     * Проверяет, что при выбрасывании исключения из метода
     * вызывается соответствующая логика в аспекте (например, логгирование).
     */
    @Test
    void testAfterThrowing_LogsWarning() {
        final Throwable ex = new RuntimeException("error");

        auditAspect.afterThrowing(joinPoint, auditable, ex);
    }
}
