FROM openjdk:21-slim-bookworm

WORKDIR /app

COPY target/audit-service-1.0.0-SNAPSHOT.jar audit-service.jar

EXPOSE 8091

ENTRYPOINT ["java", "-jar", "audit-service.jar"]
