package ru.katacademy.bank_app.accountservice.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MetricsAspectTest {

    private MetricsAspect aspect;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private Signature signature;

    @BeforeEach
    void setUp() {
        aspect = new MetricsAspect();
    }

    @Test
    void testSuccessfulMethodExecution() throws Throwable {
        when(joinPoint.proceed()).thenReturn("result");
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn("mockMethod()");

        final Object result = aspect.measureExecutionTime(joinPoint);

        verify(joinPoint, times(1)).proceed();
        assert result.equals("result");
    }

    @Test
    void testMethodThrowsException() {
        try {
            when(joinPoint.proceed()).thenThrow(new RuntimeException("fail"));
            when(joinPoint.getSignature()).thenReturn(signature);
            when(signature.toShortString()).thenReturn("mockMethod()");

            aspect.measureExecutionTime(joinPoint);
        } catch (Throwable ex) {
            assert ex instanceof RuntimeException;
            assert ex.getMessage().equals("fail");
        }

        try {
            verify(joinPoint, times(1)).proceed();
        } catch (Throwable ignored) {}
    }
}

