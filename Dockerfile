# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the jar from the builder stage
COPY --from=builder /app/target/vendor-buddy-0.0.1-SNAPSHOT.jar .

EXPOSE 8080

CMD ["java", "-jar", "vendor-buddy-0.0.1-SNAPSHOT.jar"]
