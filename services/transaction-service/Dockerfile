FROM openjdk:21-slim-bookworm

WORKDIR /app

COPY target/transaction-service-1.0.0-SNAPSHOT.jar transaction-service.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "transaction-service.jar"]
