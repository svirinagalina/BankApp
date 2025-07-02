package ru.katacademy.bank_app.accountservice.infrastructure.aspect;

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
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ValidationAspect {

    private final Validator validator;

//    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
//    public void requestMapping() {}

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void postMapping() {
    }

//    @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
//    public void putMapping() {}
//
//    @Pointcut("@annotation(org.springframework.web.bind.annotation.PatchMapping)")
//    public void patchMapping() {}
//
//    @Pointcut("@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
//    public void deleteMapping() {}
//
//    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
//    public void getMapping() {}

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
                            .collect(Collectors.toList()));
                }
            }
        }

        if (!errors.isEmpty()) {
            // Вместо возврата ResponseEntity, бросаем кастомное исключение
            throw new ValidationException(errors);
        }

        return joinPoint.proceed();
    }
}
