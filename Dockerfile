# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

# Copy the Gradle files for caching dependencies
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

# Ensure gradlew is executable and download dependencies
RUN chmod +x gradlew && ./gradlew --no-daemon dependencies

# Copy the source code and build
COPY src ./src
RUN ./gradlew clean bootJar -x test --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

# Create a system group and user for security
RUN addgroup -S asia && adduser -S asiatalent -G asia
USER asiatalent:asia

WORKDIR /app

# Copy the built JAR from Stage 1
COPY --from=builder /app/build/libs/asiakv.jar app.jar

# Optimization: Tweak JVM memory for containers
# InitialRAMPercentage/MaxRAMPercentage help Java respect Docker memory limits
ENTRYPOINT ["java", "-XX:InitialRAMPercentage=50", "-XX:MaxRAMPercentage=80", "-jar", "app.jar"]
