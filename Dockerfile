# Build stage
FROM ubuntu:latest AS build
RUN apt-get update
RUN apt-get install openjdk-17-jdk -y
WORKDIR /app
COPY . .
RUN ./mvnw package spring-boot:repackage

# Final stage
FROM openjdk:17-alpine
EXPOSE 8080
COPY --from=build /app/target/firstdockerapp-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
