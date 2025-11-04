# Use official Java image as base
FROM eclipse-temurin:21-jdk-alpine

# Set working directory inside the container
WORKDIR /app

# Copy the jar file into the container
COPY target/orderservice-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your app runs on (default Spring Boot: 8080)
EXPOSE 8083

# Command to run the jar
ENTRYPOINT ["java","-jar","app.jar"]