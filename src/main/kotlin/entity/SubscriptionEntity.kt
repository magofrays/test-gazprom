package by.magofrays.entity

import by.magofrays.dto.SubscriptionStatus
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import java.time.Duration
import java.time.Instant
import java.util.UUID


@Entity
class SubscriptionEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID?,
    var userId: String,
    var serviceId: String,
    @Enumerated(EnumType.STRING)
    var status: SubscriptionStatus,
    var startDate: Instant,
    var duration: Integer, // days
    var endDate: Instant?,

    @OneToMany(mappedBy = "subscription", cascade = [CascadeType.ALL])
    var history: MutableList<SubscriptionUpdateEntity>?,
    var updatedAt: Instant? = null,
    var createdAt: Instant? = null,
) {
    @PreUpdate
    fun preUpdate() {
        updatedAt = Instant.now()
    }

    @PrePersist
    fun prePersist() {
        createdAt = Instant.now()
        updatedAt = Instant.now()
    }
}