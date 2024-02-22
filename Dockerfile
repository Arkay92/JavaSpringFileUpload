# Use the official Maven image for a build stage
FROM maven:3.6.3-jdk-11-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Build the application
RUN mvn clean package -DskipTests

# Use the official OpenJDK image for the runtime stage
FROM openjdk:11-jre-slim
WORKDIR /app
# Copy the JAR from the build stage
COPY --from=build /app/target/chunked-file-upload-1.0.0-SNAPSHOT.jar ./app.jar
# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
