FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Cache dependencies
COPY pom.xml ./
RUN mvn -q -DskipTests dependency:go-offline

# Build
COPY src ./src
RUN mvn -q -DskipTests clean package \
	&& ls -1 target/*.jar | grep -v '\\.jar\\.original$' | grep -v 'plain\\.jar$' | head -n 1 | xargs -I{} cp {} /workspace/app.jar

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /workspace/app.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar","--server.port=8080"]
