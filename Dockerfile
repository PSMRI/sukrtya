FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the JAR file
COPY target/*.jar app.jar

# Optional ENV (values will be overridden by docker-compose)
ENV SERVER_PORT=8080

RUN mkdir -p /app/logs /app/SukrtyaImages

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
