FROM eclipse-temurin:21-jre-alpine AS runtime

RUN addgroup -S fuelup && adduser -S fuelup -G fuelup

WORKDIR /app

COPY build/libs/fuelup-tg-bot.jar app.jar

USER fuelup

EXPOSE 8081

ENTRYPOINT ["java", "-Xms128m", "-Xmx256m", "-jar", "app.jar"]
