package ru.katacademy.bank_app.settingsservice.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.katacademy.bank_shared.exception.AccountNotFoundException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Класс для обработки исключений в settings-service
 * настраивает перехватчики на уровне Spring MVC, без использования @RestControllerAdvice
 *
 * Методы:
 * - configureHandlerExceptionResolvers(): регистрирует резольверы исключений
 * - accountNotFoundResolver(): настраивает обработку AccountNotFoundException
 *
 * Автор: Быстров М.
 * Дата: 2025-05-29
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Добавляет пользовательский резольвер для перехвата исключений
     * @param resolvers списки обработчиков, которые будут применяться
     */
    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(0, accountNotFoundResolver());
    }

    /**
     * Создаёт резольвер исключения AccountNotFoundException
     * при возникновении этого исключения возвращается 404 и сообщение в формате json
     *
     * @return обработчик исключения для AccountNotFoundException
     */
    @Bean
    public HandlerExceptionResolver accountNotFoundResolver() {
        return (request, response, handler, ex) -> {
            if (ex instanceof AccountNotFoundException) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.setContentType("application/json");

                try (PrintWriter writer = response.getWriter()){
                    writer.write("{\"message\": \"" + ex.getMessage() +
                            "\", \"status\": 404}");
                    writer.flush();
                } catch (IOException ignored) {}
                return new ModelAndView();
            }
            return null;
        };
    }
}
