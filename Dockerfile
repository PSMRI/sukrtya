# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file to the container
COPY target/spring-boot-demo-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 9090

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
