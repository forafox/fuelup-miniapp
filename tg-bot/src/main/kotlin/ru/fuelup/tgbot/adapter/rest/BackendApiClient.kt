package ru.fuelup.tgbot.adapter.rest

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

@Component
class BackendApiClient(
    @Value("\${fuelup.backend.url}") backendUrl: String,
    @Value("\${fuelup.backend.internal-api-key}") apiKey: String,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val client = WebClient.builder()
        .baseUrl(backendUrl)
        .defaultHeader("X-Internal-Api-Key", apiKey)
        .build()

    fun getCustomerById(customerId: String): CustomerDto? =
        client.get()
            .uri("/internal/customers/$customerId")
            .retrieve()
            .bodyToMono<CustomerDto>()
            .doOnError { log.warn("getCustomerById failed: {}", it.message) }
            .onErrorResume { Mono.empty() }
            .block()

    fun getBonusBalance(messengerUserId: Long, platform: String): Long? =
        client.get()
            .uri("/internal/loyalty/balance?messengerId=$messengerUserId&platform=$platform")
            .retrieve()
            .bodyToMono<BalanceDto>()
            .doOnError { log.warn("getBonusBalance failed: {}", it.message) }
            .onErrorResume { Mono.empty() }
            .block()
            ?.balance

    fun getRecentOrders(messengerUserId: Long, platform: String, limit: Int = 5): List<OrderDto> =
        client.get()
            .uri("/internal/orders?messengerId=$messengerUserId&platform=$platform&limit=$limit")
            .retrieve()
            .bodyToMono<List<OrderDto>>()
            .doOnError { log.warn("getRecentOrders failed: {}", it.message) }
            .onErrorResume { Mono.empty() }
            .block() ?: emptyList()

    fun registerOrGetCustomer(
        messengerUserId: Long,
        firstName: String,
        lastName: String?,
        username: String?,
        platform: String,
    ): RegisteredCustomer {
        val body = mapOf(
            "messengerUserId" to messengerUserId,
            "firstName" to firstName,
            "lastName" to lastName,
            "username" to username,
            "platform" to platform,
        )
        return client.post()
            .uri("/internal/customers/register")
            .bodyValue(body)
            .retrieve()
            .bodyToMono<RegisteredCustomer>()
            .doOnError { log.error("registerOrGetCustomer failed: {}", it.message) }
            .onErrorResume { Mono.empty() }
            .block() ?: RegisteredCustomer("unknown", firstName, 0, "NOT_STARTED")
    }

    data class CustomerDto(val id: String, val messengerUserId: Long, val firstName: String)
    data class BalanceDto(val balance: Long)
    data class OrderDto(val orderId: String, val fuelType: String, val requestedAmount: Double, val status: String)
    data class RegisteredCustomer(val id: String, val name: String, val bonusBalance: Long, val onboardingStatus: String)
}
