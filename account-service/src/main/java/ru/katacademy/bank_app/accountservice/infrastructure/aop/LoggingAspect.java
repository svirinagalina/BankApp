package ru.katacademy.bank_app.accountservice.infrastructure.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Краткое описание: перехватывает вызовы всех методов (публичных, с любым названием, возвращаемым значением и
 * параметрами) в account-service и логирует входные параметры, результат выполнения и время работы всех публичных методов
 * при этом не раскрывает чувствительные данные (например, password, token, hash и др.).
 *
 * Методы:
 * - businessServiceMethods(): перехватывает вызовы всех методов (публичных, с любым названием, возвращаемым значением и
 * параметрами) в account-service
 * - logMethodExecution(): логирует входные параметры, результат выполнения и время работы всех публичных методов
 * при этом не раскрывает чувствительные данные (например, password, token, hash и др.).
 *
 * Автор: Пупшев Ю.Б.
 * Дата: 2025-06-24
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(public * ru.katacademy.bank_app.accountservice.application.service..*(..))")
//    @Pointcut("execution(public * *(..))")
    public void businessServiceMethods() {}

    @Around("businessServiceMethods()")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        /*
          Получаем имя класса и метода.
         */
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringType().getSimpleName() + "." + signature.getName();
        /*
         * Маскирование параметров. Каждый аргумент проходит через метод maskSensitiveData(), чтобы скрыть
         * чувствительные данные
         */
        Object[] args = Arrays.stream(joinPoint.getArgs())
                .map(this::maskSensitiveData)
                .toArray();

        log.info("-> {} Вызывается с аргументами: {}", methodName, args);
        /*
         * Вызов метода и измерение времени. Логирование результатов, маскируя результат.
         * Обработка исключений
         */
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;
            log.info("<- {} returned: {} ({}ms)", methodName, maskSensitiveData(result), duration);
            return result;
        } catch (Throwable ex) {
            long duration = System.currentTimeMillis() - start;
            log.error("<- {} threw {} after {}ms", methodName, ex.getClass().getSimpleName(), duration);
            throw ex;
        }
    }

    /**
     * Этот метод заменяет все чувствительные значения (password, token, hash)
     * в строковом представлении аргументов и возвращаемых значений на ***.
     */
    private Object maskSensitiveData(Object input) {
        if (input == null) return null;

        String str = input.toString();
        return str
                .replaceAll("(?i)(\"?password\"?\\s*[:=]\\s*)\".*?\"", "$1\"***\"")
                .replaceAll("(?i)(\"?token\"?\\s*[:=]\\s*)\".*?\"", "$1\"***\"")
                .replaceAll("(?i)(\"?hash\"?\\s*[:=]\\s*)\".*?\"", "$1\"***\"");
    }
}
