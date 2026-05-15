package ru.fuelup.tgbot.adapter.tg

import com.fasterxml.jackson.annotation.JsonProperty

data class TelegramUpdate(
    @JsonProperty("update_id") val updateId: Long,
    val message: TelegramMessage? = null,
    @JsonProperty("callback_query") val callbackQuery: CallbackQuery? = null,
)

data class TelegramMessage(
    @JsonProperty("message_id") val messageId: Long,
    val from: TelegramUser,
    val chat: TelegramChat,
    val text: String? = null,
    val date: Long,
)

data class TelegramUser(
    val id: Long,
    @JsonProperty("first_name") val firstName: String,
    @JsonProperty("last_name") val lastName: String? = null,
    val username: String? = null,
    @JsonProperty("is_bot") val isBot: Boolean = false,
)

data class TelegramChat(
    val id: Long,
    val type: String,
)

data class CallbackQuery(
    val id: String,
    val from: TelegramUser,
    val data: String? = null,
)
