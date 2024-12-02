FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install openjdk-17-jdk -y

FROM maven:3.8.5-openjdk-17 AS build

COPY . .

RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim

COPY --from=build /target/sukrtya-0.0.1-SNAPSHOT.jar sukrtya.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","sukrtya.jar"]