package by.magofrays.entity

import by.magofrays.dto.SubscriptionStatus
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.ManyToOne
import java.time.Duration
import java.time.Instant
import java.util.UUID

@Entity
class SubscriptionUpdateEntity (
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),
    @ManyToOne
    var subscription: SubscriptionEntity,
    var newStatus: SubscriptionStatus,
    var newDuration: Duration,
    var updatedAt: Instant
)