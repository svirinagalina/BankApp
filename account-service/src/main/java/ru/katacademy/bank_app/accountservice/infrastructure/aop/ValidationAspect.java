package ru.katacademy.bank_app.accountservice.infrastructure.aop;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ru.katacademy.bank_shared.exception.ValidationException;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Этот класс представляет собой аспект, который перехватывает выполнение методов, аннотированных
 * {@link org.springframework.web.bind.annotation.PostMapping}, и выполняет валидацию аргументов этих методов.
 * Если аргумент аннотирован {@link jakarta.validation.Valid}, то выполняется его валидация,
 * и если обнаружены ошибки, выбрасывается {@link ValidationException}.
 * Аспект используется для обеспечения того, чтобы входные данные, получаемые через HTTP-запросы
 * (например, POST-запросы), соответствовали ограничениям валидации.
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ValidationAspect {
    private final Validator validator;

    /**
     * Точка среза, которая перехватывает выполнение всех методов, аннотированных
     * {@link org.springframework.web.bind.annotation.PostMapping}.
     * Как расширять или изменять Pointcut
     * Чтобы включить другие HTTP-методы (например, PUT, PATCH, DELETE), создайте дополнительные методы pointcut, например:
     * Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
     * public void putMapping() {}
     * В аннотации {@code @Around} можно объединять pointcuts, например
     * Around("postMapping() || putMapping() || patchMapping()")
     * Пример возвращаемой ошибки
     * Если данные не проходят валидацию, возвращается структура:
     * "messages": [
     * "fullName: Имя не может быть пустым"
     * "email: Некорректный email адрес",
     * "password: Пароль не может быть пустым
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void postMapping() {
    }

    /**
     * Метод, который выполняет валидацию аргументов метода перед его выполнением.
     * Этот метод перехватывает выполнение методов, помеченных {@link org.springframework.web.bind.annotation.PostMapping},
     * и выполняет валидацию всех аргументов с аннотацией {@link jakarta.validation.Valid}.
     * Если валидация не проходит, выбрасывается исключение {@link ValidationException} с ошибками.
     *
     * @param joinPoint Объект, который содержит информацию о выполняемом методе и его аргументах.
     * @return Результат выполнения метода, если валидация прошла успешно.
     * @throws Throwable Исключение, если валидация не прошла.
     */
    @Around("postMapping()")
    public Object validateMethodArguments(ProceedingJoinPoint joinPoint) throws Throwable {
        final Object[] args = joinPoint.getArgs();
        final Method method = Arrays.stream(joinPoint.getTarget().getClass().getMethods())
                .filter(m -> m.getName().equals(joinPoint.getSignature().getName()))
                .findFirst().orElse(null);
        if (method == null) {
            return joinPoint.proceed();
        }
        final Parameter[] parameters = method.getParameters();
        final List<String> errors = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            final Object arg = args[i];
            if (arg == null || i >= parameters.length) {
                continue;
            }
            final boolean hasValid = Arrays.stream(parameters[i].getAnnotations())
                    .anyMatch(a -> a.annotationType().equals(Valid.class));
            if (hasValid) {
                final Set<ConstraintViolation<Object>> violations = validator.validate(arg);
                if (!violations.isEmpty()) {
                    errors.addAll(violations.stream()
                            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                            .toList());
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        return joinPoint.proceed();
    }
}
