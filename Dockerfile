# Use a base image with Maven pre-installed
FROM maven:3.8.4-openjdk-17-slim AS build
# Set the working directory in the container
WORKDIR /app
# Copy the Maven project's pom.xml file
COPY pom.xml .
# Download dependencies and build the project to cache dependencies
RUN mvn dependency:go-offline -B
# Copy the rest of the project
COPY src ./src
# Build the Maven project
RUN mvn package

# Start with a clean, lightweight image
FROM openjdk:17-jdk-slim
# Set the working directory in the container
WORKDIR /app
# Install ping command
RUN apt-get update && apt-get install -y iputils-ping
# Copy the built JAR file from the previous stage
COPY --from=build /app/target/*.jar ./app.jar
# Expose the port the application runs on
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "app.jar"]
