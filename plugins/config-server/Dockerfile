FROM openjdk:17
WORKDIR /app
COPY build/libs/config-server-0.0.1-SNAPSHOT.jar app.jar
COPY config-repo/ /config-repo/
ENTRYPOINT ["java", "-jar", "app.jar"]
