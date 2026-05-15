package ru.fuelup.maxbot.adapter.rest

import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodyOrNull
import ru.fuelup.maxbot.usecase.RegisteredCustomer

@ConfigurationProperties(prefix = "fuelup.backend")
data class BackendProperties(
    val url: String,
    val internalApiKey: String,
)

@Component
class BackendApiClient(props: BackendProperties) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val client = WebClient.builder()
        .baseUrl(props.url)
        .defaultHeader("X-Internal-Api-Key", props.internalApiKey)
        .build()

    suspend fun getCustomerById(customerId: String): CustomerDto? =
        runCatching {
            client.get()
                .uri("/internal/customers/$customerId")
                .retrieve()
                .awaitBodyOrNull<CustomerDto>()
        }.onFailure { log.error("getCustomerById failed: {}", it.message) }.getOrNull()

    suspend fun getBonusBalance(messengerUserId: Long, platform: String): Long? =
        runCatching {
            client.get()
                .uri("/internal/loyalty/balance?messengerId=$messengerUserId&platform=$platform")
                .retrieve()
                .awaitBodyOrNull<BalanceDto>()
                ?.balance
        }.onFailure { log.warn("getBonusBalance failed: {}", it.message) }.getOrNull()

    suspend fun getRecentOrders(messengerUserId: Long, platform: String, limit: Int): List<OrderDto>? =
        runCatching {
            client.get()
                .uri("/internal/orders?messengerId=$messengerUserId&platform=$platform&limit=$limit")
                .retrieve()
                .awaitBodyOrNull<List<OrderDto>>()
        }.onFailure { log.warn("getRecentOrders failed: {}", it.message) }.getOrNull()

    suspend fun registerOrGetCustomer(
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
            .awaitBodyOrNull<RegisteredCustomer>()
            ?: RegisteredCustomer(
                id = "unknown",
                name = firstName,
                bonusBalance = 0,
                onboardingStatus = "NOT_STARTED",
            )
    }

    data class CustomerDto(val id: String, val messengerUserId: Long, val firstName: String)
    data class BalanceDto(val balance: Long)
    data class OrderDto(val orderId: String, val fuelType: String, val requestedAmount: Double, val status: String)
}
