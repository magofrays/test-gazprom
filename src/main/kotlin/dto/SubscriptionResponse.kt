package by.magofrays.dto

import java.time.Instant
import java.util.*

data class SubscriptionResponse(
    val id: UUID,
    val userId: String,
    val service: String,
    val startDate: Instant,
    val duration: Integer,
    val status: SubscriptionStatus,
    val updateStatusDate: Instant,
    val endDate: Instant,
    val createdAt: Instant,
    val updatedAt: Instant
)