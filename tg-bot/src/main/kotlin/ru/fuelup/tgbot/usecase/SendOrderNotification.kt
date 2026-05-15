package ru.fuelup.tgbot.usecase

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.fuelup.tgbot.adapter.rest.BackendApiClient
import ru.fuelup.tgbot.adapter.tg.TelegramApiClient

/**
 * Отправляет пользователю уведомление об изменении статуса заказа.
 * Вызывается из основного сервера через REST API (X-Internal-Api-Key).
 */
@Service
class SendOrderNotification(
    private val backendApi: BackendApiClient,
    private val telegramApi: TelegramApiClient,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun invoke(customerId: String, orderId: String, status: String, actualAmount: Double?) {
        val customer = backendApi.getCustomerById(customerId) ?: run {
            log.warn("Customer not found: {}", customerId)
            return
        }

        val text = buildNotificationText(status, orderId, actualAmount)
        telegramApi.sendMessage(chatId = customer.messengerUserId, text = text)

        log.info("Order notification sent: customerId={} orderId={} status={}", customerId, orderId, status)
    }

    private fun buildNotificationText(status: String, orderId: String, actualAmount: Double?): String {
        return when (status) {
            "PAID" -> "✅ Оплата прошла! Ваш заказ #${orderId.takeLast(6)} принят в обработку."
            "COMPLETED" -> buildString {
                append("⛽ Заправка завершена!")
                if (actualAmount != null) {
                    append(" Отпущено: %.2f л.".format(actualAmount))
                }
            }
            "CANCELLED" -> "❌ Заказ #${orderId.takeLast(6)} отменён."
            "FAILED" -> "⚠️ При обработке заказа #${orderId.takeLast(6)} произошла ошибка. Средства возвращены."
            else -> "📋 Статус заказа #${orderId.takeLast(6)} обновлён: $status"
        }
    }
}
