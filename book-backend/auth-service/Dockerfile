# Build stage
FROM maven:3.9-eclipse-temurin-17-alpine AS build

# Install protobuf compiler and tools
RUN apk add --no-cache protobuf protobuf-dev curl bash

# Download and install protoc-gen-grpc-java plugin from Maven repository
RUN curl -L -o /usr/local/bin/protoc-gen-grpc-java \
    https://repo1.maven.org/maven2/io/grpc/protoc-gen-grpc-java/1.54.1/protoc-gen-grpc-java-1.54.1-linux-x86_64.exe && \
    chmod +x /usr/local/bin/protoc-gen-grpc-java

# Verify the plugin is properly installed
RUN ls -la /usr/local/bin/protoc-gen-grpc-java
RUN test -x /usr/local/bin/protoc-gen-grpc-java || exit 1

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

# Copy source code for auth-service
COPY auth-service/src ./auth-service/src

# Install required libraries for protoc
RUN apk add --no-cache \
    libstdc++ \
    libc6-compat \
    gcompat

# Build the service
RUN mvn clean package -DskipTests -pl auth-service -am

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/auth-service/target/auth-service-1.0.0.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]