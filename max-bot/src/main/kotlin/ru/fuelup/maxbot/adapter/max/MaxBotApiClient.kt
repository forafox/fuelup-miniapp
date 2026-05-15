package ru.fuelup.maxbot.adapter.max

import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitBodilessEntity
import kotlinx.coroutines.reactor.awaitSingleOrNull

@ConfigurationProperties(prefix = "max.bot")
data class MaxBotProperties(
    val token: String,
    val apiUrl: String = "https://botapi.max.ru",
)

@Component
class MaxBotApiClient(private val props: MaxBotProperties) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val client = WebClient.builder()
        .baseUrl(props.apiUrl)
        .defaultHeader("Authorization", "Bearer ${props.token}")
        .build()

    suspend fun sendMessage(chatId: Long, text: String, keyboard: List<List<KeyboardButton>>? = null): Boolean {
        return try {
            val body = buildMap {
                put("recipient", mapOf("chat_id" to chatId))
                put("type", "text")
                put("body", mapOf("text" to text))
                if (keyboard != null) {
                    put("attachments", listOf(mapOf(
                        "type" to "inline_keyboard",
                        "payload" to mapOf("buttons" to keyboard)
                    )))
                }
            }
            client.post()
                .uri("/messages")
                .bodyValue(body)
                .retrieve()
                .awaitBodilessEntity()
            true
        } catch (e: Exception) {
            log.error("Failed to send MAX message to chatId={}: {}", chatId, e.message)
            false
        }
    }

    suspend fun answerCallback(callbackId: String, notification: String? = null) {
        try {
            client.post()
                .uri("/answers")
                .bodyValue(mapOf("callback_id" to callbackId, "notification" to notification))
                .retrieve()
                .awaitBodilessEntity()
        } catch (e: Exception) {
            log.warn("Failed to answer callback {}: {}", callbackId, e.message)
        }
    }

    data class KeyboardButton(val text: String, val payload: String? = null)
}
