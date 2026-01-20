package ru.katacademy.bank_app.accountservice.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.katacademy.bank_shared.aspect.RetryableOperation;

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
        final MethodSignature sig = (MethodSignature) pjp.getSignature();
        final var anno = sig.getMethod().getAnnotation(RetryableOperation.class);

        final int maxAttempts = calculateMaxAttempts(anno);
        final long delay = calculateDelay(anno);
        final var retryOn = anno.retryOn();

        return executeWithRetry(pjp, sig, maxAttempts, delay, retryOn);
    }

    private int calculateMaxAttempts(RetryableOperation anno) {
        final int maxAttempts;
        if (anno.maxAttempts() > 0) {
            maxAttempts = anno.maxAttempts();
        } else {
            maxAttempts = defaultMaxAttempts;
        }
        return maxAttempts;
    }

    private long calculateDelay(RetryableOperation anno) {
        final long delay;
        if (anno.backoffDelayMs() > 0) {
            delay = anno.backoffDelayMs();
        } else {
            delay = defaultBackoffMs;
        }
        return delay;
    }

    private Object executeWithRetry(ProceedingJoinPoint pjp, MethodSignature sig,
                                    int maxAttempts, long delay,
                                    Class<? extends Throwable>[] retryOn) throws Throwable {
        int attempt = 0;

        while (true) {
            try {
                return pjp.proceed();
            } catch (Throwable ex) {
                attempt++;
                logRetryAttempt(pjp, sig, attempt, maxAttempts, ex);

                if (!shouldRetry(ex, retryOn) || attempt >= maxAttempts) {
                    throw ex;
                }

                Thread.sleep(delay);
            }
        }
    }

    private void logRetryAttempt(ProceedingJoinPoint pjp, MethodSignature sig,
                                 int attempt, int maxAttempts, Throwable ex) {
        if (log.isErrorEnabled()) {
            log.error(
                    "[{}.{}] attempt {}/{} args={} -> {}",
                    pjp.getTarget().getClass().getSimpleName(),
                    sig.getMethod().getName(),
                    attempt, maxAttempts,
                    pjp.getArgs(), ex.toString(), ex
            );
        }
    }

    private boolean shouldRetry(Throwable ex, Class<? extends Throwable>[] retryOn) {
        for (var cls : retryOn) {
            if (cls.isAssignableFrom(ex.getClass())) {
                return true;
            }
        }
        return false;
    }
}