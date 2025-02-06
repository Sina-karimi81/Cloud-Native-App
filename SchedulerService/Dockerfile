FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY ./target/SchedulerService-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8082
CMD ["java", "-jar", "app.jar"]