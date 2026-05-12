FROM openjdk:21-slim-bookworm

WORKDIR /app

COPY target/notification-service-1.0.0-SNAPSHOT.jar notification-service.jar

EXPOSE 8088

ENTRYPOINT ["java", "-jar", "notification-service.jar"]
