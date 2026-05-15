package ru.fuelup.tgbot.usecase

import org.springframework.stereotype.Service
import ru.fuelup.tgbot.adapter.rest.BackendApiClient

data class RegisteredCustomer(
    val id: String,
    val name: String,
    val bonusBalance: Long,
    val onboardingStatus: String,
    val messengerUserId: Long,
)

@Service
class RegisterCustomer(private val backendApi: BackendApiClient) {

    fun invoke(
        messengerUserId: Long,
        firstName: String,
        lastName: String?,
        username: String?,
        platform: String,
    ): RegisteredCustomer {
        val dto = backendApi.registerOrGetCustomer(messengerUserId, firstName, lastName, username, platform)
        return RegisteredCustomer(
            id = dto.id,
            name = dto.name,
            bonusBalance = dto.bonusBalance,
            onboardingStatus = dto.onboardingStatus,
            messengerUserId = messengerUserId,
        )
    }
}
