FROM ubuntu:latest
LABEL authors="SUKHROB"

FROM openjdk:17-jdk-slim

COPY target/card-service.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
