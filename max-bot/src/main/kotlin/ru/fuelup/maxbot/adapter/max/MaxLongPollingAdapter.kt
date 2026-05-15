package ru.fuelup.maxbot.adapter.max

import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import ru.fuelup.maxbot.domain.MaxUpdate
import ru.fuelup.maxbot.usecase.HandleMaxUpdate

@ConfigurationProperties(prefix = "max.polling")
data class MaxPollingProperties(
    val timeout: Int = 20,
    val limit: Int = 100,
)

@Component
@ConditionalOnProperty(name = ["max.mode"], havingValue = "polling", matchIfMissing = true)
class MaxLongPollingAdapter(
    private val botProps: MaxBotProperties,
    private val pollingProps: MaxPollingProperties,
    private val handleUpdate: HandleMaxUpdate,
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val client = WebClient.builder()
        .baseUrl(botProps.apiUrl)
        .defaultHeader("Authorization", "Bearer ${botProps.token}")
        .build()

    @EventListener(ApplicationReadyEvent::class)
    fun startPolling() {
        log.info("Starting MAX long polling (timeout={}s)", pollingProps.timeout)
        scope.launch { poll() }
    }

    private suspend fun poll() {
        var marker: Long? = null

        while (isActive) {
            try {
                val response = client.get()
                    .uri { builder ->
                        builder.path("/updates")
                        builder.queryParam("timeout", pollingProps.timeout)
                        builder.queryParam("limit", pollingProps.limit)
                        if (marker != null) builder.queryParam("marker", marker)
                        builder.build()
                    }
                    .retrieve()
                    .awaitBody<UpdatesResponse>()

                response.updates.forEach { update ->
                    launch { handleUpdate.invoke(update) }
                }

                if (response.updates.isNotEmpty()) {
                    marker = response.marker
                }
            } catch (e: CancellationException) {
                break
            } catch (e: Exception) {
                log.error("Long polling error: ${e.message}. Retrying in 5s...")
                delay(5_000)
            }
        }
    }

    private data class UpdatesResponse(
        val updates: List<MaxUpdate>,
        val marker: Long? = null,
    )
}
