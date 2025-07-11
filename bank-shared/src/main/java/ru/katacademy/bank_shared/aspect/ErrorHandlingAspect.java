package ru.katacademy.bank_shared.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Аспект обработки ошибок и автоматических ретраев.
 * Перехватывает методы, помеченные @RetryableOperation, логирует ошибки и
 * повторяет выполнение при возникновении указанных исключений.
 * Параметры maxAttempts и backoffDelayMs могут задаваться через аннотацию
 * или через настройки приложения (<code>application.yml</code>).
 *
 * <p>Пример в application.yml:
 * <pre>
 * retry:
 *   max-attempts: 5
 *   backoff-delay-ms: 1000
 * </pre>
 * </p>
 *
 * @author Глеб Ткачёв
 */
@Aspect
@Component
public class ErrorHandlingAspect {
    private static final Logger log = LoggerFactory.getLogger(ErrorHandlingAspect.class);

    @Value("${retry.max-attempts:3}")
    private int defaultMaxAttempts;

    @Value("${retry.backoff-delay-ms:500}")
    private long defaultBackoffMs;

    /**
     * Основной advice, оборачивающий выполнение целевого метода.
     * Выполняет повторные попытки при указанных исключениях.
     *
     * @param pjp контекст выполнения метода
     * @return результат метода
     * @throws Throwable если все попытки завершились неудачей
     * @author Глеб Ткачёв
     */
    @Around("@annotation(ru.katacademy.bank_shared.aspect.RetryableOperation)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature sig = (MethodSignature) pjp.getSignature();
        var anno = sig.getMethod().getAnnotation(RetryableOperation.class);

        int maxAttempts = anno.maxAttempts() > 0 ? anno.maxAttempts() : defaultMaxAttempts;
        long delay = anno.backoffDelayMs() > 0 ? anno.backoffDelayMs() : defaultBackoffMs;
        var retryOn = anno.retryOn();

        int attempt = 0;
        while (true) {
            try {
                return pjp.proceed();
            } catch (Throwable ex) {
                attempt++;
                log.error(
                        "[{}.{}] attempt {}/{} args={} -> {}",
                        pjp.getTarget().getClass().getSimpleName(),
                        sig.getMethod().getName(),
                        attempt, maxAttempts,
                        pjp.getArgs(), ex.toString(), ex
                );

                boolean shouldRetry = false;
                for (var cls : retryOn) {
                    if (cls.isAssignableFrom(ex.getClass())) {
                        shouldRetry = true;
                        break;
                    }
                }
                if (!shouldRetry || attempt >= maxAttempts) {
                    throw ex;
                }
                Thread.sleep(delay);
            }
        }
    }
}