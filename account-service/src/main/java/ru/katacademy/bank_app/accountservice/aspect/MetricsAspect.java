package ru.katacademy.bank_app.accountservice.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Аспект для сбора метрик выполнения бизнес-методов сервисов.
 * <p>
 * Основные функции:
 * <ul>
 *   <li>Измеряет время выполнения метода (в миллисекундах)</li>
 *   <li>Логирует успешные и неуспешные вызовы методов</li>
 *   <li>Перехватывает все методы в пакете application.service</li>
 * </ul>
 */

@Aspect
@Component
public class MetricsAspect {

    private static final Logger logger = LoggerFactory.getLogger(MetricsAspect.class);

    @Around("execution(* ru.katacademy.bank_app.accountservice.application.service..*(..))")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        final long start = System.nanoTime();
        try {
            final Object result = joinPoint.proceed();
            final long duration = (System.nanoTime() - start) / 1_000_000;
            logger.info("Метод {} выполнен успешно за {} мс", joinPoint.getSignature().toShortString(), duration);
            return result;
        } catch (Throwable ex) {
            final long duration = (System.nanoTime() - start) / 1_000_000;
            logger.error("Метод {} завершился с ошибкой через {} мс: {}", joinPoint.getSignature().toShortString(), duration, ex.getMessage());
            throw ex;
        }
    }
}

