package ru.fuelup.loadtest

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import ru.fuelup.loadtest.scenarios.{GasStationScenario, OrderFlowScenario}

import scala.concurrent.duration._

class FuelUpSimulation extends Simulation {

  private val httpProtocol = http
    .baseUrl(Config.baseUrl)
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")
    .userAgentHeader("Gatling/FuelUp-LoadTest")
    .shareConnections

  // ступенчатая нагрузка согласно разделу 3.1 ВКР
  private val steppedInjection = Config.rampUsers.flatMap { case (users, dur) =>
    Seq(rampUsers(users) during dur.seconds)
  }

  setUp(
    GasStationScenario.scenario
      .inject(steppedInjection: _*)
      .protocols(httpProtocol),

    OrderFlowScenario.scenario
      .inject(
        nothingFor(30.seconds),
        steppedInjection: _*
      )
      .protocols(httpProtocol)
  ).assertions(
    global.responseTime.percentile(95).lt(2000),
    global.successfulRequests.percent.gt(95)
  )
}
