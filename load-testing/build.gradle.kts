plugins {
    id("io.gatling.gradle") version "3.11.5.2"
}

gatling {
    enterprise {
        // no-op for local runs
    }
}

dependencies {
    gatling("io.gatling.highcharts:gatling-charts-highcharts:3.11.5")
}
