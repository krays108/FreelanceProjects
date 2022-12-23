FROM maven:3.6.3-openjdk-11-slim as builder

WORKDIR /app
COPY pom.xml .
# Use this optimization to cache the local dependencies. Works as long as the POM doesn't change
RUN mvn dependency:go-offline

COPY src/ /app/src/
RUN mvn package -Dspring.profiles.active=container

# Use AdoptOpenJDK for base image.
FROM openjdk:11-jre-slim

# Copy the jar to the production image from the builder stage.
COPY --from=builder /app/target/*.jar /app.jar

CMD ["java", "-jar", "/app.jar"]

