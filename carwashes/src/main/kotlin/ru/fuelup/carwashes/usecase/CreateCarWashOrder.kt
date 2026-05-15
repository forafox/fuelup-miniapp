package ru.fuelup.carwashes.usecase

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.fuelup.carwashes.domain.CarWashOrder
import ru.fuelup.carwashes.usecase.port.CarWashOrderRepository
import ru.fuelup.carwashes.usecase.port.CarWashPartnerPort
import ru.fuelup.carwashes.usecase.port.CarWashPaymentPort
import ru.fuelup.carwashes.usecase.port.CarWashStationRepository
import java.util.UUID

data class CreateOrderCommand(
    val customerId: UUID,
    val stationId: UUID,
    val boxNumber: Int,
    val programId: Long,
    val platform: String,
)

sealed class CreateCarWashOrderResult {
    data class Success(val order: CarWashOrder) : CreateCarWashOrderResult()
    data class StationNotFound(val stationId: UUID) : CreateCarWashOrderResult()
    data class BoxUnavailable(val boxNumber: Int) : CreateCarWashOrderResult()
    data class ProgramNotFound(val programId: Long) : CreateCarWashOrderResult()
    object PartnerError : CreateCarWashOrderResult()
    object PaymentError : CreateCarWashOrderResult()
}

@Service
class CreateCarWashOrder(
    private val stationRepo: CarWashStationRepository,
    private val orderRepo: CarWashOrderRepository,
    private val partnerPort: CarWashPartnerPort,
    private val paymentPort: CarWashPaymentPort,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun invoke(command: CreateOrderCommand): CreateCarWashOrderResult {
        val station = stationRepo.findById(command.stationId)
            ?: return CreateCarWashOrderResult.StationNotFound(command.stationId)

        val box = station.boxes.find { it.number == command.boxNumber }
        if (box == null || box.status != ru.fuelup.carwashes.domain.WashBox.BoxStatus.FREE) {
            return CreateCarWashOrderResult.BoxUnavailable(command.boxNumber)
        }

        val program = station.programs.find { it.id == command.programId }
            ?: return CreateCarWashOrderResult.ProgramNotFound(command.programId)

        val price = program.discountedPrice ?: program.price

        // предварительно сохраняем заказ — если партнёр упадёт, запись в БД сохранится
        val draft = orderRepo.save(
            CarWashOrder(
                id = UUID.randomUUID(),
                externalOrderId = null,
                stationId = station.id,
                boxNumber = command.boxNumber,
                programId = command.programId,
                customerId = command.customerId,
                status = CarWashOrder.OrderStatus.PENDING,
                price = price,
                paymentUrl = null,
                platform = command.platform,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
            )
        )

        val partnerResult = partnerPort.createOrder(
            internalOrderId = draft.id,
            stationExternalId = station.externalId,
            boxNumber = command.boxNumber,
            programName = program.name,
            price = price,
        )

        if (!partnerResult.isSuccess) {
            log.error("Partner refused carwash order: stationId={} box={}", station.id, command.boxNumber)
            orderRepo.updateStatus(draft.id, CarWashOrder.OrderStatus.FAILED)
            return CreateCarWashOrderResult.PartnerError
        }

        val withPartner = orderRepo.updateExternalId(draft.id, partnerResult.externalOrderId!!)

        val paymentResult = paymentPort.createPayment(draft.id, price.toLong())
        if (!paymentResult.isSuccess) {
            orderRepo.updateStatus(draft.id, CarWashOrder.OrderStatus.FAILED)
            return CreateCarWashOrderResult.PaymentError
        }

        val finalOrder = orderRepo.updatePaymentUrl(draft.id, paymentResult.paymentUrl!!)
        return CreateCarWashOrderResult.Success(finalOrder)
    }
}
