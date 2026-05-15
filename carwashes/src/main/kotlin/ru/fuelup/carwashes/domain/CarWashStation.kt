package ru.fuelup.carwashes.domain

import java.util.UUID

data class CarWashStation(
    val id: UUID,
    val externalId: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val status: StationStatus,
    val boxes: List<WashBox>,
    val programs: List<WashProgram>,
    val workingHours: WorkingHours?,
    val distanceMeters: Int? = null,
) {
    fun isOpen(): Boolean {
        if (status != StationStatus.ACTIVE) return false
        return workingHours?.isCurrentlyOpen() ?: true
    }

    fun availableBoxes() = boxes.filter { it.status == WashBox.BoxStatus.FREE }
}

enum class StationStatus { ACTIVE, INACTIVE, MAINTENANCE }

data class WashBox(
    val id: Long,
    val number: Int,
    val status: BoxStatus,
    val type: BoxType,
) {
    enum class BoxStatus { FREE, BUSY, OFFLINE }
    enum class BoxType { TOUCHLESS, BRUSH, SELF_SERVICE }
}

data class WashProgram(
    val id: Long,
    val name: String,
    val description: String,
    val durationMinutes: Int,
    val price: Int,
    val discountedPrice: Int?,
)

data class WorkingHours(
    val from: String,  // "09:00"
    val until: String, // "22:00"
    val is24h: Boolean = false,
) {
    fun isCurrentlyOpen(): Boolean {
        if (is24h) return true
        val now = java.time.LocalTime.now()
        val open = java.time.LocalTime.parse(from)
        val close = java.time.LocalTime.parse(until)
        return now.isAfter(open) && now.isBefore(close)
    }
}
