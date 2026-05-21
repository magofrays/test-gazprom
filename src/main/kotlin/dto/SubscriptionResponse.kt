package by.magofrays.dto

import java.time.Instant
import java.util.*

data class SubscriptionResponse(
    val id: UUID,
    val userId: String,
    val serviceId: String,
    val startDate: Instant,
    val duration: Int,
    val status: SubscriptionStatus,
    val endDate: Instant,
    val createdAt: Instant,
    val updatedAt: Instant
)