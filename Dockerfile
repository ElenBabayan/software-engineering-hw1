# Use an OpenJDK base image
FROM eclipse-temurin:17-jre

# Create a working directory
WORKDIR /app

# Expose the port that Nats service listens on (e.g., 8081)
EXPOSE 8081

ENTRYPOINT ["java", "-jar"]
