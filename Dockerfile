# Stage 1: Build the application
FROM eclipse-temurin:17-jdk-alpine AS builder

# Set working directory
WORKDIR /app

# Copy maven/gradle files first (for better caching)
COPY pom.xml ./
# If using Gradle, uncomment these instead:
# COPY build.gradle ./
# COPY settings.gradle ./

# Copy source code
COPY src ./src

# If using Maven
RUN apk add --no-cache maven && mvn clean package -DskipTests

# If using Gradle, comment out Maven line above and uncomment this:
# RUN apk add --no-cache gradle && gradle build --no-daemon

# Stage 2: Create runtime image
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the port your Spring Boot app runs on (default 8080)
EXPOSE 8080

# Set environment variables (optional - customize as needed)
ENV JAVA_OPTS=""

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
