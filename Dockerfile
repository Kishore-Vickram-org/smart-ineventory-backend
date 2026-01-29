# Base image with Java 17
FROM eclipse-temurin:17-jdk-alpine

# Set working directory inside container
WORKDIR /app

# Copy Spring Boot JAR into container
COPY target/*.jar app.jar

# Expose application port
EXPOSE 8088

# Run Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
