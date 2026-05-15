FROM eclipse-temurin:21-jre-alpine AS runtime

RUN addgroup -S fuelup && adduser -S fuelup -G fuelup

WORKDIR /app

COPY build/libs/fuelup-carwashes.jar app.jar

USER fuelup

EXPOSE 8083

ENTRYPOINT ["java", "-Xms128m", "-Xmx256m", "-jar", "app.jar"]
