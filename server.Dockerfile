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

# Build the application
RUN ./gradlew classes

# Expose the port the app runs on
EXPOSE 50051

# Run the application
CMD ["./gradlew", "runServer"]
