# Образ с Eclipse Temurin вместо OpenJDK для уменьшения размера образа
FROM eclipse-temurin:21-jre-alpine

# Рабочая директорию
WORKDIR /app

# Скачиваем агент OpenTelemetry с использованием ADD
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.15.0/opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar

# Копируем JAR приложения
COPY build/libs/security-service-*.jar app.jar

# Команда запуска приложения с подключенным агентом
ENTRYPOINT ["java", "-javaagent:/app/opentelemetry-javaagent.jar", "-jar", "app.jar"]
