FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy the EXACT jar name
COPY target/inventory-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
