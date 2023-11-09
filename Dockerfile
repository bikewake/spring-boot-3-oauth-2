FROM eclipse-temurin:17.0.9_9-jre-jammy

# Set the working directory in the container
WORKDIR /app

# Create a directory for configuration files
RUN mkdir /app/config

# Copy the application JAR
COPY target/chat-0.0.1-SNAPSHOT.jar /app/app.jar

# Set the volume for configuration files
VOLUME /app/config

# Set the environment variable for the exposed port
ENV PORT 8090

# Expose the port that your Spring Boot app will run on
EXPOSE $PORT

# Specify the command to run on container startup
CMD ["java", "-jar", "app.jar", "--spring.config.location=classpath:/,file:/app/config/", "--spring.profiles.active=chat"]
