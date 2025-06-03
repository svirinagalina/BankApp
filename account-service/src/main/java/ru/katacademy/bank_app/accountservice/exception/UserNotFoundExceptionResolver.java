package ru.katacademy.bank_app.accountservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import ru.katacademy.bank_shared.exception.UserNotFoundException;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * - UserNotFoundException обработчик исключений, реализует HandlerExceptionResolver
 * без использования @RestControllerAdvice и WebMvcConfigurer. Автоматическая регистрация
 * благодаря анотации @Component
 * Возвращает статус 404 при UserNotFoundException. Ответ JSON.
 * Если не UserNotFoundException, то управление другим резольверам.
 *
 * Автор:Быстров М
 * Дат: 30.05.2025
 */
@Component
public class UserNotFoundExceptionResolver implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Object handler,
                                         Exception ex) {

        if (ex instanceof UserNotFoundException) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            try (PrintWriter writer = response.getWriter()) {
                writer.write("{\"message\": \"" + ex.getMessage() + "\", \"status\": 404}");
                writer.flush();
            } catch (IOException ignored) {}

            return new ModelAndView(); // пустая заглушка (иначе 500)
        }

        return null;
    }
}
