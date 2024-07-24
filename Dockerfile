# Use an official OpenJDK runtime that supports Java 22
FROM openjdk:22-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the Gradle wrapper and build files into the container
COPY gradlew /app/
COPY gradle /app/gradle/
COPY build.gradle /app/
COPY settings.gradle /app/

# Copy the source code into the container
COPY src /app/src

# Install Gradle
RUN apt-get update && apt-get install -y wget unzip
RUN wget https://services.gradle.org/distributions/gradle-8.0-bin.zip
RUN unzip gradle-8.0-bin.zip -d /opt
ENV PATH=$PATH:/opt/gradle-8.0/bin

# Build the application
RUN ./gradlew build

# Expose the port the app runs on
EXPOSE 50051

# Run the application
CMD ["./gradlew", "run"]
