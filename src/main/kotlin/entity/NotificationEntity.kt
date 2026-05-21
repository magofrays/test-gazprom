package by.magofrays.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.PrePersist
import org.springframework.data.annotation.Id
import java.time.Instant
import java.util.UUID

@Entity
class NotificationEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),
    var userId: String,
    var message: String,
    var createdAt: Instant = Instant.now()
) {
    @PrePersist
    fun prePersist() {
        createdAt = Instant.now()
    }
}