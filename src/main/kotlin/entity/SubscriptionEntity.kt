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
    val id: UUID = UUID.randomUUID(),
    var userId: String,
    var serviceId: String,
    @Enumerated(EnumType.STRING)
    var status: SubscriptionStatus,
    var startDate: Instant,
    var duration: Duration,
    var endDate: Instant,

    @OneToMany(mappedBy = "subscription", cascade = [CascadeType.ALL])
    var history: MutableList<SubscriptionUpdateEntity> = mutableListOf(),  // ← MutableList

    var updatedAt: Instant = Instant.now(),
    var createdAt: Instant = Instant.now(),
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