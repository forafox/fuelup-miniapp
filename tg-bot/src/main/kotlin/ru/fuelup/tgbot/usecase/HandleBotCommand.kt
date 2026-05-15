package ru.fuelup.tgbot.usecase

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.fuelup.tgbot.adapter.rest.BackendApiClient
import ru.fuelup.tgbot.adapter.tg.TelegramApiClient
import ru.fuelup.tgbot.adapter.tg.TelegramMessage

@Service
class HandleBotCommand(
    private val telegramApi: TelegramApiClient,
    private val backendApi: BackendApiClient,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun invoke(message: TelegramMessage) {
        val chatId = message.chat.id
        val userId = message.from.id

        when (message.text?.substringBefore(' ')?.lowercase()) {
            "/balance" -> handleBalance(chatId, userId)
            "/orders" -> handleOrders(chatId, userId)
            "/help" -> handleHelp(chatId)
            else -> telegramApi.sendMessage(chatId, "Неизвестная команда. Введите /help для справки.")
        }
    }

    private fun handleBalance(chatId: Long, userId: Long) {
        val balance = backendApi.getBonusBalance(userId, "TELEGRAM")
        if (balance != null) {
            telegramApi.sendMessage(chatId, "💎 Ваш бонусный баланс: <b>$balance</b> бонусов")
        } else {
            telegramApi.sendMessage(chatId, "Не удалось получить баланс. Попробуйте позже.")
        }
    }

    private fun handleOrders(chatId: Long, userId: Long) {
        val orders = backendApi.getRecentOrders(userId, "TELEGRAM", limit = 5)
        if (orders.isEmpty()) {
            telegramApi.sendMessage(chatId, "У вас пока нет заказов.")
            return
        }

        val text = buildString {
            append("<b>Последние заказы:</b>\n\n")
            orders.forEach { order ->
                append("• #${order.orderId.takeLast(6)} ")
                append("${order.fuelType} ${order.requestedAmount} л — ")
                append(localizeStatus(order.status))
                append("\n")
            }
        }
        telegramApi.sendMessage(chatId, text)
    }

    private fun handleHelp(chatId: Long) {
        val text = """
            <b>Доступные команды:</b>

            /balance — бонусный баланс
            /orders — последние заказы
            /help — эта справка
        """.trimIndent()
        telegramApi.sendMessage(chatId, text)
    }

    private fun localizeStatus(status: String) = when (status) {
        "PENDING" -> "ожидает"
        "PLACED" -> "размещён"
        "PAID" -> "оплачен"
        "COMPLETED" -> "завершён"
        "CANCELLED" -> "отменён"
        "FAILED" -> "ошибка"
        else -> status
    }
}
