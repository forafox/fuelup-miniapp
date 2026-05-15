package ru.fuelup.maxbot.usecase

import org.springframework.stereotype.Service
import ru.fuelup.maxbot.adapter.rest.BackendApiClient

data class RegisteredCustomer(
    val id: String,
    val name: String,
    val bonusBalance: Long,
    val onboardingStatus: String,
)

@Service
class RegisterMaxCustomer(private val backendApi: BackendApiClient) {

    suspend fun invoke(
        messengerUserId: Long,
        name: String,
        username: String?,
        platform: String,
    ): RegisteredCustomer {
        // Бот не имеет доступа к initData напрямую, поэтому регистрация
        // происходит через внутренний API (X-Internal-Api-Key) при получении BOT_STARTED
        return backendApi.registerOrGetCustomer(
            messengerUserId = messengerUserId,
            firstName = name.substringBefore(" "),
            lastName = name.substringAfter(" ", "").ifEmpty { null },
            username = username,
            platform = platform,
        )
    }
}
