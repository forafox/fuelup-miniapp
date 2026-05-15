package ru.fuelup.carwashes.web

import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import ru.fuelup.carwashes.domain.CarWashStation
import ru.fuelup.carwashes.usecase.CreateCarWashOrder
import ru.fuelup.carwashes.usecase.CreateCarWashOrderResult
import ru.fuelup.carwashes.usecase.CreateOrderCommand
import ru.fuelup.carwashes.usecase.port.CarWashStationRepository
import java.util.UUID

@RestController
@RequestMapping("/api/v1/carwashes")
@Validated
class CarWashStationEndpoint(
    private val stationRepo: CarWashStationRepository,
    private val createOrder: CreateCarWashOrder,
) {

    @GetMapping
    fun getNearby(
        @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") latitude: Double,
        @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") longitude: Double,
        @RequestParam(defaultValue = "5000") radiusMeters: Int,
    ): ResponseEntity<List<StationResponse>> {
        val stations = stationRepo.findNearby(latitude, longitude, radiusMeters)
        return ResponseEntity.ok(stations.map { StationResponse.from(it) })
    }

    @GetMapping("/{stationId}")
    fun getById(@PathVariable stationId: UUID): ResponseEntity<StationResponse> {
        val station = stationRepo.findById(stationId)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(StationResponse.from(station))
    }

    @PostMapping("/orders")
    fun createOrder(
        @RequestBody request: CreateOrderRequest,
        @RequestHeader("X-Customer-Id") customerId: UUID,
        @RequestHeader("X-Platform") platform: String,
    ): ResponseEntity<Any> {
        val command = CreateOrderCommand(
            customerId = customerId,
            stationId = request.stationId,
            boxNumber = request.boxNumber,
            programId = request.programId,
            platform = platform,
        )

        return when (val result = createOrder.invoke(command)) {
            is CreateCarWashOrderResult.Success ->
                ResponseEntity.ok(OrderResponse(
                    orderId = result.order.id,
                    paymentUrl = result.order.paymentUrl,
                    status = result.order.status.name,
                    price = result.order.price,
                ))
            is CreateCarWashOrderResult.StationNotFound ->
                ResponseEntity.notFound().build()
            is CreateCarWashOrderResult.BoxUnavailable ->
                ResponseEntity.unprocessableEntity()
                    .body(mapOf("error" to "BOX_UNAVAILABLE", "boxNumber" to result.boxNumber))
            is CreateCarWashOrderResult.PartnerError, CreateCarWashOrderResult.PaymentError ->
                ResponseEntity.internalServerError()
                    .body(mapOf("error" to "SERVICE_UNAVAILABLE"))
            else -> ResponseEntity.unprocessableEntity().build()
        }
    }
}

data class CreateOrderRequest(
    val stationId: UUID,
    val boxNumber: Int,
    val programId: Long,
)

data class StationResponse(
    val id: UUID,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val status: String,
    val isOpen: Boolean,
    val availableBoxes: Int,
    val programs: List<ProgramResponse>,
    val distanceMeters: Int?,
) {
    companion object {
        fun from(s: CarWashStation) = StationResponse(
            id = s.id,
            name = s.name,
            address = s.address,
            latitude = s.latitude,
            longitude = s.longitude,
            status = s.status.name,
            isOpen = s.isOpen(),
            availableBoxes = s.availableBoxes().size,
            programs = s.programs.map { ProgramResponse(it.id, it.name, it.price, it.discountedPrice) },
            distanceMeters = s.distanceMeters,
        )
    }
}

data class ProgramResponse(val id: Long, val name: String, val price: Int, val discountedPrice: Int?)
data class OrderResponse(val orderId: UUID, val paymentUrl: String?, val status: String, val price: Int)
