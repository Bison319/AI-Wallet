FROM openjdk:21-slim-bookworm

WORKDIR /app

COPY target/fraud-detection-service-1.0.0-SNAPSHOT.jar fraud-detection-service.jar

EXPOSE 8086

ENTRYPOINT ["java", "-jar", "fraud-detection-service.jar"]
