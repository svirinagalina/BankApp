# Используем образ с JDK
FROM openjdk:21

# Указываем рабочую директорию
WORKDIR /app

# Копируем собранный jar-файл в контейнер
COPY build/libs/bank-app.jar app.jar

# Команда запуска
ENTRYPOINT ["java", "-jar", "app.jar"]
