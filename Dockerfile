FROM openjdk:21-slim

WORKDIR /app

# Copy the JAR file
COPY target/*.jar app.jar

# Environment variables with default values
ENV DB_IP=${DB_IP}
ENV DB_PORT=${DB_PORT}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV DB_NAME=${DB_NAME}

ENV SERVER_PORT=${SERVER_PORT}
ENV SPRING_APPLICATION_NAME=${SPRING_APPLICATION_NAME}

# Create logs directory
RUN mkdir -p /app/logs

# Create logs directory
RUN mkdir -p /app/SukrtyaImages

# Expose the port
EXPOSE ${SERVER_PORT}

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]