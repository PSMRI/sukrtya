FROM openjdk:17-jdk-slim AS build

COPY . .

RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim

COPY --from=build /target/sukrtya-1.1.jar sukrtya.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","sukrtya.jar"]
