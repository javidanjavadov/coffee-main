FROM openjdk:21-slim

WORKDIR /app

# Copy the application JAR file
COPY build/libs/coffee-shop-application-all.jar app.jar

# Create a directory for the application configuration
RUN mkdir -p /app/config

# Copy the application configuration
COPY src/main/resources/application.yaml /app/config/

# Expose the application port
EXPOSE 8080

# Set the environment variable for the configuration file
ENV KTOR_CONFIG_FILE=/app/config/application.yaml

# Run the application
CMD ["java", "-jar", "app.jar"]
