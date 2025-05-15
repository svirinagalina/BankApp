# Образ с Eclipse Temurin вместо OpenJDK для уменьшения размера образа
FROM eclipse-temurin:21-jre-alpine

# Рабочая директория внутри контейнера
WORKDIR /app

# Копируем сборку jar (предположим, fat jar)
COPY build/libs/app.jar app.jar


# Команда запуска
ENTRYPOINT ["java", "-jar", "app.jar"]