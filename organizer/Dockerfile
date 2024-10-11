FROM openjdk:17-jdk-alpine
ARG JAR-File=target/*.jar
COPY ./target/organizer-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]