package ru.fuelup.carwashes.domain

import java.util.UUID

data class CarWashOrder(
    val id: UUID,
    val externalOrderId: String?,
    val stationId: UUID,
    val boxNumber: Int,
    val programId: Long,
    val customerId: UUID,
    val status: OrderStatus,
    val price: Int,
    val paymentUrl: String?,
    val platform: String,
    val createdAt: Long,
    val updatedAt: Long,
) {
    fun isTerminal() = status in setOf(OrderStatus.COMPLETED, OrderStatus.CANCELLED, OrderStatus.FAILED)

    enum class OrderStatus {
        PENDING,
        PLACED,
        PAID,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED,
        FAILED,
    }
}
