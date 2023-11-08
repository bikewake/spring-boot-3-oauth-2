FROM eclipse-temurin:17.0.9_9-jre-jammy
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]