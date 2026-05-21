package by.magofrays.repository

import by.magofrays.entity.SubscriptionUpdateEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SubscriptionUpdateRepository : JpaRepository<SubscriptionUpdateEntity, UUID> {

}