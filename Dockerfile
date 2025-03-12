FROM openjdk:21-slim

WORKDIR /app

# Copy the JAR file
COPY target/*.jar app.jar

# Environment variables with default values
ENV SPRING_DATASOURCE_URL=jdbc:${SPRING_DATASOURCE_URL}
ENV SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME}
ENV SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD}
ENV SERVER_PORT=${SERVER_PORT}
ENV SPRING_APPLICATION_NAME=${SPRING_APPLICATION_NAME}

# Create logs directory
RUN mkdir -p /app/logs

# Expose the port
EXPOSE ${SERVER_PORT}

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]