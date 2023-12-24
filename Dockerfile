FROM ubuntu:latest AS build
RUN apt-get update
RUN apt-get install openjdk-17-jdk -y
COPY . .
RUN  ./mvnw package spring-boot:repackage

FROM openjdk:17-jdk-slim
EXPOSE 8080

ENTRYPOINT ["java","-jar","./target/firstdockerapp-0.0.1-SNAPSHOT.jar"]