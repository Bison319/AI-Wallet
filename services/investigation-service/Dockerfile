FROM openjdk:21-slim-bookworm

WORKDIR /app

COPY target/investigation-service-1.0.0-SNAPSHOT.jar investigation-service.jar

EXPOSE 8087

ENTRYPOINT ["java", "-jar", "investigation-service.jar"]
