package ru.fuelup.tgbot.web

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.fuelup.tgbot.usecase.SendOrderNotification

@RestController
@RequestMapping("/internal/notify")
class NotificationController(
    private val sendOrderNotification: SendOrderNotification,
) {

    @PostMapping("/order-status")
    fun orderStatus(
        @RequestBody request: OrderStatusRequest,
        @RequestHeader("X-Internal-Api-Key") apiKey: String,
    ): ResponseEntity<Void> {
        sendOrderNotification.invoke(
            customerId = request.customerId,
            orderId = request.orderId,
            status = request.status,
            actualAmount = request.actualAmount,
        )
        return ResponseEntity.noContent().build()
    }

    data class OrderStatusRequest(
        val customerId: String,
        val orderId: String,
        val status: String,
        val actualAmount: Double?,
    )
}
