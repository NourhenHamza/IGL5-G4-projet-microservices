# Use a valid OpenJDK 17 image
FROM eclipse-temurin:17-jdk-focal

# Set working directory
WORKDIR /app

# Copy the built jar from target
COPY target/*.jar app.jar

# Expose port 8082 (as your app.properties defines)
EXPOSE 8082

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
