FROM openjdk:21-slim-bookworm

WORKDIR /app

COPY target/wallet-service-1.0.0-SNAPSHOT.jar wallet-service.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "wallet-service.jar"]
