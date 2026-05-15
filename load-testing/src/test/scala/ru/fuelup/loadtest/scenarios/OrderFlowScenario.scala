package ru.fuelup.loadtest.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.ScenarioBuilder

object OrderFlowScenario {

  private val fuelTypes = Array("AI92", "AI95", "AI98", "DT")

  val scenario: ScenarioBuilder = scenario("OrderCreationFlow")
    // шаг 1: получить список ближайших АЗС
    .exec(
      http("GET /gas-stations nearby")
        .get("/api/v1/gas-stations/nearby")
        .queryParam("lat", "55.7558")
        .queryParam("lon", "37.6173")
        .queryParam("radius", "3000")
        .header("Authorization", "Bearer #{jwt}")
        .check(status.is(200))
        .check(jsonPath("$[0].id").saveAs("stationId"))
        .check(jsonPath("$[0].columns[0].id").saveAs("columnId"))
        .check(jsonPath("$[0].columns[0].number").saveAs("columnNumber"))
        .check(jsonPath("$[0].fuels[0].basePrice").saveAs("fuelPrice"))
    )
    .pause(1, 2)
    // шаг 2: создать заказ
    .exec(
      http("POST /orders")
        .post("/api/v1/orders")
        .header("Authorization", "Bearer #{jwt}")
        .header("Content-Type", "application/json")
        .body(StringBody(session =>
          s"""{
             |  "gasStationId": "${session("stationId").as[String]}",
             |  "columnId": ${session("columnId").as[String]},
             |  "columnNumber": ${session("columnNumber").as[String]},
             |  "fuelType": "${fuelTypes(scala.util.Random.nextInt(fuelTypes.length))}",
             |  "requestedAmount": ${10 + scala.util.Random.nextInt(40)},
             |  "fuelPrice": ${session("fuelPrice").as[String]},
             |  "paymentType": "SBP"
             |}""".stripMargin
        ))
        .check(status.in(200, 201, 422))
        .check(jsonPath("$.id").optional.saveAs("orderId"))
    )
    .pause(2, 5)
}
