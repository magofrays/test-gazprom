package by.magofrays.dto

import java.time.Duration
import java.time.Instant
import java.util.UUID

data class SubscriptionResponse (
    val id: UUID,
    val userId: String,
    val service: String,
    val startDate: Instant,
    val duration: Duration,
    val status: SubscriptionStatus,
    val updateStatusDate: Instant,
    val endDate: Instant,
    val createdAt: Instant,
    val updatedAt: Instant
)