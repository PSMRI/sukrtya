# ---- Build Stage ----
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy pom and src
COPY pom.xml .
COPY src ./src

# Build the jar
RUN ./mvnw -q -DskipTests package || mvn -q -DskipTests package

# ---- Runtime Stage ----
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

RUN mkdir -p /app/logs /app/SukrtyaImages

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
