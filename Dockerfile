# Образ с Eclipse Temurin вместо OpenJDK для уменьшения размера образа
FROM eclipse-temurin:21-jre-alpine

# Рабочая директорию
WORKDIR /app

# Скачиваем агент OpenTelemetry и сохраняем внутрь Docker-образа, в папку /app
RUN curl -o /app/opentelemetry-javaagent.jar \
  https://github.com/open-telemetry/opentelemetry-java/releases/download/v1.17.0/opentelemetry-javaagent-1.17.0.jar

# Копируем JAR приложения
COPY build/libs/app.jar app.jar

# Команда запуска приложения с подключенным агентом
ENTRYPOINT ["java", "-javaagent:/app/opentelemetry-javaagent.jar", "-jar", "app.jar"]
