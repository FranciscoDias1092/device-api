FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/deviceapi-0.0.1-SNAPSHOT.jar deviceapi.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "deviceapi.jar"]