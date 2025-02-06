FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY ./target/ConsumerService-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8081
CMD ["java", "-jar", "app.jar"]