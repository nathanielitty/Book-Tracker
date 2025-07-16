# Build stage
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy parent pom and all service poms
COPY pom.xml .
COPY api-gateway/pom.xml ./api-gateway/
COPY auth-service/pom.xml ./auth-service/
COPY book-service/pom.xml ./book-service/
COPY library-service/pom.xml ./library-service/
COPY notification-service/pom.xml ./notification-service/
COPY analytics-service/pom.xml ./analytics-service/

# Copy proto files
COPY proto ./proto

# Copy source code for api-gateway
COPY api-gateway/src ./api-gateway/src

# Build the service
RUN mvn clean package -DskipTests -pl api-gateway -am

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/api-gateway/target/api-gateway-1.0.0.jar app.jar

# Add health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
