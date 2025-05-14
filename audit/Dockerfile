# Образ с Eclipse Temurin вместо OpenJDK для уменьшения размера образа
FROM eclipse-temurin:21-jre-alpine

# Рабочая директорию
WORKDIR /app

# Копируем JAR приложения
COPY build/libs/*.jar app.jar

# Команда запуска приложения с подключенным агентом
ENTRYPOINT ["java", "-javaagent:/app/opentelemetry-javaagent.jar", "-jar", "app.jar"]
