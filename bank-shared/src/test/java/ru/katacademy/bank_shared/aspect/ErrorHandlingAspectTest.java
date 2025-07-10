package ru.katacademy.bank_shared.aspect;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.KafkaException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit-тест для {@link ErrorHandlingAspect}.
 * Проверяет логику автоматических ретраев и логирования:
 * <ul>
 *   <li>Повтор до успешного выполнения;</li>
 *   <li>Превышение лимита попыток и проброс исключения;</li>
 *   <li>Отсутствие ретраев для неретраемых исключений.</li>
 * </ul>
 */
class ErrorHandlingAspectTest {

    @InjectMocks
    private ErrorHandlingAspect aspect;

    @Mock
    private ProceedingJoinPoint pjp;

    @Mock
    private MethodSignature signature;

    private final TestService target = new TestService();

    /**
     * Инициализация Mockito и моки ProceedingJoinPoint.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(pjp.getSignature()).thenReturn(signature);
        when(pjp.getTarget()).thenReturn(target);
    }

    /**
     * Должен ретраить и вернуть результат на второй попытке.
     */
    @Test
    void shouldRetryAndSucceedOnSecondAttempt() throws Throwable {
        when(signature.getMethod()).thenReturn(
                TestService.class.getMethod("unstableOp")
        );
        when(pjp.proceed())
                .thenThrow(new DataAccessException("DB down") {})
                .thenReturn("OK");

        Object result = aspect.around(pjp);

        assertEquals("OK", result);
        verify(pjp, times(2)).proceed();
    }

    /**
     * Должен пробросить KafkaException после 3 неудачных попыток.
     */
    @Test
    void shouldExceedMaxAttemptsAndThrow() throws Throwable {
        when(signature.getMethod()).thenReturn(
                TestService.class.getMethod("unstableOp")
        );
        when(pjp.proceed()).thenThrow(new KafkaException("Kafka down"));

        Throwable ex = assertThrows(KafkaException.class,
                () -> aspect.around(pjp)
        );
        assertTrue(ex.getMessage().contains("Kafka down"));
        verify(pjp, times(3)).proceed();
    }

    /**
     * Неретраемая ошибка должна сразу пробрасываться без повторных попыток.
     */
    @Test
    void shouldNotRetryOnNonRetryableException() throws Throwable {
        when(signature.getMethod()).thenReturn(
                TestService.class.getMethod("unstableOp")
        );
        when(pjp.proceed()).thenThrow(new NullPointerException("oops"));

        assertThrows(NullPointerException.class,
                () -> aspect.around(pjp)
        );
        verify(pjp, times(1)).proceed();
    }

    /**
     * Вспомогательный класс для тестирования аспекта.
     */
    static class TestService {
        @RetryableOperation(maxAttempts = 3, backoffDelayMs = 10,
                retryOn = {DataAccessException.class, KafkaException.class})
        public String unstableOp() { return ""; }
    }
}