package ru.fuelup.maxbot.usecase

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.fuelup.maxbot.adapter.max.MaxBotApiClient
import ru.fuelup.maxbot.adapter.rest.BackendApiClient
import ru.fuelup.maxbot.domain.MaxUpdate
import ru.fuelup.maxbot.domain.UpdateType

@Service
class HandleMaxUpdate(
    private val botApi: MaxBotApiClient,
    private val backendApi: BackendApiClient,
    private val registerCustomer: RegisterMaxCustomer,
    private val sendWelcome: SendWelcomeMessage,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    suspend fun invoke(update: MaxUpdate) {
        when (update.type) {
            UpdateType.BOT_STARTED -> {
                val user = update.message?.sender ?: return
                val customer = registerCustomer.invoke(
                    messengerUserId = user.userId,
                    name = user.name,
                    username = user.username,
                    platform = "MAX",
                )
                sendWelcome.invoke(update.message.recipient.chatId, customer)
            }

            UpdateType.MESSAGE_CREATED -> {
                val msg = update.message ?: return
                val text = msg.body.text ?: return

                when {
                    text == "/start" || text == "Начать" -> {
                        val customer = registerCustomer.invoke(
                            messengerUserId = msg.sender.userId,
                            name = msg.sender.name,
                            username = msg.sender.username,
                            platform = "MAX",
                        )
                        sendWelcome.invoke(msg.recipient.chatId, customer)
                    }
                    text == "/balance" || text == "Мои бонусы" -> {
                        handleBalanceRequest(msg.recipient.chatId, msg.sender.userId)
                    }
                    text == "/orders" -> {
                        handleOrdersRequest(msg.recipient.chatId, msg.sender.userId)
                    }
                    else -> log.debug("Unhandled text: {}", text)
                }
            }

            UpdateType.MESSAGE_CALLBACK -> {
                val cb = update.callback ?: return
                botApi.answerCallback(cb.callbackId)
                // TODO: обработка callback-кнопок
            }

            else -> log.trace("Ignored update type: {}", update.type)
        }
    }

    private suspend fun handleBalanceRequest(chatId: Long, messengerUserId: Long) {
        val balance = backendApi.getBonusBalance(messengerUserId, "MAX")
        val text = if (balance != null) {
            "💰 Ваш бонусный баланс: *${balance}* бонусов"
        } else {
            "Не удалось получить данные. Попробуйте позже."
        }
        botApi.sendMessage(chatId, text)
    }

    private suspend fun handleOrdersRequest(chatId: Long, messengerUserId: Long) {
        val orders = backendApi.getRecentOrders(messengerUserId, "MAX", limit = 3)
        if (orders.isNullOrEmpty()) {
            botApi.sendMessage(chatId, "У вас пока нет заказов. Откройте мини-приложение, чтобы оформить первую заправку!")
            return
        }

        val text = buildString {
            appendLine("📋 Последние заказы:")
            orders.forEach { order ->
                appendLine("• ${order.fuelType}, ${order.requestedAmount} л — ${order.status}")
            }
        }
        botApi.sendMessage(chatId, text)
    }
}
