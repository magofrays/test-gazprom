package by.magofrays.dto

import java.time.Instant

data class SubscriptionFilterRequest (
    val userId: String?,
    val status: SubscriptionStatus?,
    val serviceId: String?,
    val dateFrom: Instant?,
    val dateTo: Instant?
)