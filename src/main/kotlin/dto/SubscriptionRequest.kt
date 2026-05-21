package by.magofrays.dto

import by.magofrays.validation.CreateGroup
import by.magofrays.validation.UpdateGroup
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.time.Duration
import java.time.Instant


data class SubscriptionRequest(
    @NotNull(groups = [CreateGroup::class], message = "userId is required for creation")
    val userId: String?,

    @NotNull(groups = [CreateGroup::class], message = "serviceId is required for creation")
    val serviceId: String?,

    @NotNull(groups = [CreateGroup::class], message = "status is required for creation")
    val status: SubscriptionStatus?,

    @NotNull(groups = [CreateGroup::class], message = "startDate is required for creation")
    val startDate: Instant?,

    @NotNull(groups = [CreateGroup::class], message = "duration is required for creation")
    @Positive(groups = [CreateGroup::class, UpdateGroup::class], message = "duration must be positive")
    val duration: Int?
)
