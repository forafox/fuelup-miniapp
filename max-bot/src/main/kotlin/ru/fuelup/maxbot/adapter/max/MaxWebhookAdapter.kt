package ru.fuelup.maxbot.adapter.max

import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.fuelup.maxbot.domain.MaxUpdate
import ru.fuelup.maxbot.usecase.HandleMaxUpdate

// Webhook-режим используется в prod, long polling — для локальной разработки.
// Переключение через: max.mode=webhook | polling

@RestController
@RequestMapping("/webhook/max")
@ConditionalOnProperty(name = ["max.mode"], havingValue = "webhook")
class MaxWebhookAdapter(
    private val handleUpdate: HandleMaxUpdate,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping
    fun receiveUpdate(@RequestBody update: MaxUpdate): ResponseEntity<Void> {
        log.debug("MAX webhook update: type={} updateId={}", update.type, update.updateId)
        // блокирующий вызов допустим, т.к. Loom virtual threads
        runBlocking { handleUpdate.invoke(update) }
        return ResponseEntity.ok().build()
    }
}
