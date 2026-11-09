# ==========================================
# Stage 1: Build the Application
# ==========================================
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /build

COPY pom.xml ./

RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# ==========================================
# Stage 2: Runtime Image
# ==========================================
FROM eclipse-temurin:21-jre-jammy AS runtime

WORKDIR /app

# Create a non-root system user for security best practices
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Copy the built JAR from the builder stage
COPY --from=builder /build/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Configure JVM options for containerized environments
ENTRYPOINT ["java", \
            "-XX:+UseContainerSupport", \
            "-XX:MaxRAMPercentage=75.0", \
            "-jar", \
            "app.jar"]