FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -q -DskipTests package

# ---- Runtime Stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
RUN mkdir -p /app/logs /app/SukrtyaImages
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
