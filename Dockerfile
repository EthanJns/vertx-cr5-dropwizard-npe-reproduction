FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/starter-1.0.0-SNAPSHOT-fat.jar /app/starter-1.0.0-SNAPSHOT-fat.jar

CMD ["java", "-Dvertx.metrics.options.enabled=true", "-jar", "starter-1.0.0-SNAPSHOT-fat.jar"]