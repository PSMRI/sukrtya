# ---------- Build Stage ----------
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml and download dependencies first (cache optimization)
COPY pom.xml .
RUN mvn -q dependency:go-offline


COPY src ./src

# Build application
RUN mvn -q -DskipTests package


# ---------- Run Stage ----------
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built JAR
COPY --from=build /app/target/*.jar app.jar

# Create necessary folders
RUN mkdir -p /app/logs /app/SukrtyaImages

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
