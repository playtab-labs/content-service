FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app
COPY build/libs/contentservice-*.jar app.jar

EXPOSE 9096

ENTRYPOINT ["java", "-jar", "app.jar"]
