package ru.fuelup.tgbot.usecase

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.fuelup.tgbot.adapter.tg.TelegramApiClient

@Service
class SendWelcomeMessage(
    private val telegramApi: TelegramApiClient,
    @Value("\${telegram.mini-app-url}") private val miniAppUrl: String,
) {

    fun invoke(chatId: Long, customer: RegisteredCustomer) {
        val text = buildString {
            append("Привет, <b>${customer.name}</b>! 👋\n\n")
            append("Я помогу вам заправиться прямо из Telegram.\n")
            append("Бонусный счёт: <b>${customer.bonusBalance}</b> бонусов\n\n")
            append("Нажмите кнопку ниже, чтобы открыть мини-приложение.")
        }

        val keyboard = TelegramApiClient.InlineKeyboard(
            inline_keyboard = listOf(
                listOf(TelegramApiClient.InlineButton(text = "⛽ Открыть FuelUp", url = miniAppUrl))
            )
        )

        telegramApi.sendMessageWithKeyboard(chatId, text, keyboard)
    }
}
