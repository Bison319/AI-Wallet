FROM openjdk:21-slim-bookworm

WORKDIR /app

COPY target/payment-service-1.0.0-SNAPSHOT.jar payment-service.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "payment-service.jar"]
