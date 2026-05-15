package ru.fuelup.maxbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableAsync
@EnableScheduling
@ConfigurationPropertiesScan
class MaxBotApplication

fun main(args: Array<String>) {
    runApplication<MaxBotApplication>(*args)
}
