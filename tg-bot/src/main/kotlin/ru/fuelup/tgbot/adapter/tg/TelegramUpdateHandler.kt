package ru.fuelup.tgbot.adapter.tg

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.fuelup.tgbot.usecase.HandleBotCommand
import ru.fuelup.tgbot.usecase.RegisterCustomer
import ru.fuelup.tgbot.usecase.SendWelcomeMessage

@RestController
@RequestMapping("/webhook/telegram")
class TelegramWebhookController(
    private val handleCommand: HandleBotCommand,
    private val registerCustomer: RegisterCustomer,
    private val sendWelcome: SendWelcomeMessage,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping
    fun handleUpdate(@RequestBody update: TelegramUpdate) {
        log.debug("Received update: updateId={}", update.updateId)

        val message = update.message ?: return

        when {
            message.text == "/start" -> {
                val customer = registerCustomer.invoke(
                    messengerUserId = message.from.id,
                    firstName = message.from.firstName,
                    lastName = message.from.lastName,
                    username = message.from.username,
                    platform = "TELEGRAM"
                )
                sendWelcome.invoke(message.chat.id, customer)
            }
            message.text?.startsWith("/") == true -> {
                handleCommand.invoke(message)
            }
        }
    }
}
