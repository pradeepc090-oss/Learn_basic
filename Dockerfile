# syntax=docker/dockerfile:1
# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
# Cache mount keeps ~/.m2 between builds so dependencies download only once.
RUN --mount=type=cache,target=/root/.m2 mvn -B -q dependency:resolve
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B clean package

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app
COPY --from=build /workspace/target/sample-app.jar app.jar
USER app
EXPOSE 8080
ENV PORT=8080
HEALTHCHECK --interval=30s --timeout=3s CMD wget -qO- http://localhost:8080/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
