FROM openjdk:21-slim-bookworm

WORKDIR /app

COPY target/analytics-service-1.0.0-SNAPSHOT.jar analytics-service.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "analytics-service.jar"]
