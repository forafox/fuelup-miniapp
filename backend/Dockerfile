FROM eclipse-temurin:21-jre-alpine AS runtime

RUN addgroup -S fuelup && adduser -S fuelup -G fuelup

WORKDIR /app

COPY infrastructure/build/libs/fuelup-backend.jar app.jar

USER fuelup

EXPOSE 8080

ENTRYPOINT ["java", \
  "-XX:+UseZGC", \
  "-XX:+ZGenerational", \
  "-Xms256m", "-Xmx512m", \
  "-jar", "app.jar"]
