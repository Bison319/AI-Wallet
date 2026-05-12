FROM openjdk:21-slim-bookworm

WORKDIR /app

COPY target/discovery-server-1.0.0-SNAPSHOT.jar discovery-server.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "discovery-server.jar"]
