FROM openjdk:11-jre-slim

WORKDIR /app

COPY mvnw* .
COPY pom.xml .
COPY .mvn/ .mvn/
COPY src/ src/