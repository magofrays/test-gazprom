package by.magofrays.entity

import by.magofrays.dto.SubscriptionStatus
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.time.Duration
import java.time.Instant
import java.util.UUID

@Entity
class SubscriptionUpdateEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    @ManyToOne
    var subscription: SubscriptionEntity,
    @Enumerated(EnumType.STRING)
    var newStatus: SubscriptionStatus,
    var newDuration: Int, //days
    var updatedAt: Instant? = null
)