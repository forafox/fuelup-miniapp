package ru.fuelup.loadtest.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import ru.fuelup.loadtest.Config

object GasStationScenario {

  // координаты вокруг центра Москвы со случайным смещением ±0.05°
  private val lat = () => 55.75 + (math.random() - 0.5) * 0.1
  private val lon = () => 37.62 + (math.random() - 0.5) * 0.1

  val scenario = scenario("NearbyGasStations")
    .exec(
      http("GET /gas-stations")
        .get("/api/v1/gas-stations/nearby")
        .queryParam("lat", _ => lat().toString)
        .queryParam("lon", _ => lon().toString)
        .queryParam("radius", "5000")
        .header("Authorization", "Bearer #{jwt}")
        .check(status.is(200))
        .check(jsonPath("$").exists)
    )
    .pause(1, 3)
}
