FROM eclipse-temurin:21-jre-alpine AS runtime

RUN addgroup -S fuelup && adduser -S fuelup -G fuelup

WORKDIR /app

COPY build/libs/fuelup-max-bot.jar app.jar

USER fuelup

EXPOSE 8082

ENTRYPOINT ["java", "-Xms128m", "-Xmx256m", "-jar", "app.jar"]
