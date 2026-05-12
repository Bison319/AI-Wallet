FROM openjdk:21-slim-bookworm

WORKDIR /app

COPY target/user-service-1.0.0-SNAPSHOT.jar user-service.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "user-service.jar"]
