package ru.fuelup.maxbot.domain

import com.fasterxml.jackson.annotation.JsonProperty

// MAX Bot API возвращает обновления в несколько ином формате чем Telegram,
// поэтому маппинг выделен в отдельный доменный тип

data class MaxUpdate(
    @JsonProperty("update_id") val updateId: Long,
    val timestamp: Long,
    val type: UpdateType,
    val message: MaxMessage? = null,
    @JsonProperty("callback") val callback: MaxCallback? = null,
)

enum class UpdateType {
    @JsonProperty("message_created") MESSAGE_CREATED,
    @JsonProperty("message_callback") MESSAGE_CALLBACK,
    @JsonProperty("bot_started") BOT_STARTED,
    @JsonProperty("user_added") USER_ADDED,
}

data class MaxMessage(
    val body: MessageBody,
    val sender: MaxUser,
    val recipient: MaxChat,
    val timestamp: Long,
)

data class MessageBody(
    @JsonProperty("mid") val messageId: String,
    val text: String? = null,
)

data class MaxUser(
    @JsonProperty("user_id") val userId: Long,
    val name: String,
    val username: String? = null,
)

data class MaxChat(
    @JsonProperty("chat_id") val chatId: Long,
    val type: String,
)

data class MaxCallback(
    @JsonProperty("callback_id") val callbackId: String,
    val payload: String? = null,
    val user: MaxUser,
    val timestamp: Long,
)
