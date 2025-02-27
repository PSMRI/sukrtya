FROM maven:3.8.5-openjdk-17 AS build

COPY . .

RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim

COPY --from=build /target/sukrtya-1.1.jar sukrtya.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","sukrtya.jar"]
