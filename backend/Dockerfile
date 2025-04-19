# Stage 1 - Build
FROM maven:3.8-eclipse-temurin-17 AS build-stage
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests

# Stage 2 - Run
FROM eclipse-temurin:17-jdk
COPY --from=build-stage /app/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]