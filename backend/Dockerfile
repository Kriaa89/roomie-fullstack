# 🧱 Stage 1: Build the Spring Boot Application
FROM maven:3.8-eclipse-temurin-17 AS build-stage
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests

# 🚀 Stage 2: Use minimal image for production
FROM eclipse-temurin:17-jdk-alpine
RUN apk --no-cache upgrade
COPY --from=build-stage /app/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
