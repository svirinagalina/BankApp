package ru.katacademy.bank_app.accountservice.infrastructure.aspect;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
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
//
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

        // Проверяем аргументы на наличие ошибок валидации
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

        // Если ошибки есть, возвращаем 400 и список ошибок
        if (!errors.isEmpty()) {
            final Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("status", HttpStatus.BAD_REQUEST.value());
            responseBody.put("errors", errors);  // Вернуть ошибки
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        // Если ошибок нет, возвращаем успешный ответ, но также с пустым массивом "errors"
        final Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", HttpStatus.OK.value());
        responseBody.put("errors", Collections.emptyList());  // Пустой массив
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}
