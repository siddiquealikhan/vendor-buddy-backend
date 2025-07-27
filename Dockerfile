# 🧱 Stage 1: Build the JAR file
FROM maven:3.9.6-eclipse-temurin-17 as builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# 🧱 Stage 2: Run the JAR
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy only the built JAR from the builder stage
COPY --from=builder /app/target/vendor-buddy-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
