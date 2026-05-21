package by.magofrays.repository

import by.magofrays.dto.SubscriptionStatus
import by.magofrays.entity.SubscriptionEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*

@Repository
interface SubscriptionRepository :
    JpaRepository<SubscriptionEntity, UUID>,
    JpaSpecificationExecutor<SubscriptionEntity> {
    @Query(
        "SELECT s FROM SubscriptionEntity s " +
                "WHERE (:userId IS NULL OR s.userId = :userId) " +
                "AND (:status IS NULL OR s.status = :status) " +
                "AND (:serviceId IS NULL OR s.serviceId = :serviceId) " +
                "AND (:dateFrom IS NULL OR s.startDate >= :dateFrom) " +
                "AND (:dateTo IS NULL OR s.startDate <= :dateTo)"
    )
    fun findAllByFilter(
        pageable: Pageable,
        @Param("userId") userId: String?,
        @Param("status") status: SubscriptionStatus?,
        @Param("serviceId") serviceId: String?,
        @Param("dateFrom") dateFrom: Instant?,
        @Param("dateTo") dateTo: Instant?
    ): Page<SubscriptionEntity>

    fun findAllByEndDateBeforeAndStatus(date : Instant, status : SubscriptionStatus) : List<SubscriptionEntity>

    fun findAllByEndDateBetweenAndStatus(startDate: Instant, endDate: Instant, status: SubscriptionStatus) : List<SubscriptionEntity>
}
