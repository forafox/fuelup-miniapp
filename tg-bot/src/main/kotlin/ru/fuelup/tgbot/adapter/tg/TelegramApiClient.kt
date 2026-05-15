package ru.fuelup.tgbot.adapter.tg

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

@Component
class TelegramApiClient(
    @Value("\${telegram.bot.token}") token: String,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val client = WebClient.builder()
        .baseUrl("https://api.telegram.org/bot$token")
        .build()

    fun sendMessage(chatId: Long, text: String, parseMode: String = "HTML") {
        client.post()
            .uri("/sendMessage")
            .bodyValue(mapOf(
                "chat_id" to chatId,
                "text" to text,
                "parse_mode" to parseMode,
            ))
            .retrieve()
            .bodyToMono<Map<*, *>>()
            .doOnError { log.error("sendMessage failed: chatId={} error={}", chatId, it.message) }
            .onErrorResume { Mono.empty() }
            .subscribe()
    }

    fun sendMessageWithKeyboard(chatId: Long, text: String, keyboard: InlineKeyboard) {
        client.post()
            .uri("/sendMessage")
            .bodyValue(mapOf(
                "chat_id" to chatId,
                "text" to text,
                "parse_mode" to "HTML",
                "reply_markup" to keyboard,
            ))
            .retrieve()
            .bodyToMono<Map<*, *>>()
            .doOnError { log.error("sendMessageWithKeyboard failed: chatId={}", chatId) }
            .onErrorResume { Mono.empty() }
            .subscribe()
    }

    fun answerCallbackQuery(callbackQueryId: String, text: String? = null) {
        client.post()
            .uri("/answerCallbackQuery")
            .bodyValue(buildMap {
                put("callback_query_id", callbackQueryId)
                if (text != null) put("text", text)
            })
            .retrieve()
            .bodyToMono<Map<*, *>>()
            .onErrorResume { Mono.empty() }
            .subscribe()
    }

    data class InlineKeyboard(val inline_keyboard: List<List<InlineButton>>)
    data class InlineButton(val text: String, val callback_data: String? = null, val url: String? = null)
}
