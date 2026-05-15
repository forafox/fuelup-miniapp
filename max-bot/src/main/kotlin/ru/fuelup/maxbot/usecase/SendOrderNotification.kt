package ru.fuelup.maxbot.usecase

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.fuelup.maxbot.adapter.max.MaxBotApiClient
import ru.fuelup.maxbot.adapter.rest.BackendApiClient

@Service
class SendOrderNotification(
    private val backendApi: BackendApiClient,
    private val botApi: MaxBotApiClient,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    suspend fun invoke(customerId: String, orderId: String, status: String, actualAmount: Double?) {
        val customer = backendApi.getCustomerById(customerId)
        if (customer == null) {
            log.warn("Cannot send notification — customer not found: {}", customerId)
            return
        }

        val text = formatStatusText(status, orderId, actualAmount)
        val chatId = customer.messengerUserId

        val sent = botApi.sendMessage(chatId, text)
        if (!sent) {
            log.error("Failed to deliver MAX notification: customerId={} orderId={}", customerId, orderId)
        }
    }

    private fun formatStatusText(status: String, orderId: String, actualAmount: Double?) = when (status) {
        "PAID"      -> "✅ Оплата получена! Подъезжайте к колонке #${orderId.takeLast(4)}."
        "COMPLETED" -> buildString {
            append("⛽ Заправка завершена!")
            actualAmount?.let { append(" Объём: %.2f л.".format(it)) }
        }
        "CANCELLED" -> "❌ Заказ отменён. Если возникли вопросы — напишите в поддержку."
        "FAILED"    -> "⚠️ Ошибка при обработке заказа. Средства не списаны."
        else        -> "📋 Статус заказа обновлён: $status"
    }
}
