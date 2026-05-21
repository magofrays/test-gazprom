package by.magofrays.service

import by.magofrays.dto.SubscriptionFilterRequest
import by.magofrays.dto.SubscriptionRequest
import by.magofrays.dto.SubscriptionResponse
import by.magofrays.dto.SubscriptionStatus
import by.magofrays.entity.SubscriptionEntity
import by.magofrays.entity.SubscriptionUpdateEntity
import by.magofrays.exception.BusinessException
import by.magofrays.mapper.SubscriptionMapper
import by.magofrays.repository.SubscriptionRepository
import by.magofrays.repository.SubscriptionUpdateRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit


@Service
open class SubscriptionService @Autowired constructor(
    val subscriptionRepository: SubscriptionRepository,
    val subscriptionUpdateRepository: SubscriptionUpdateRepository,
    val subscriptionMapper: SubscriptionMapper, private val notificationService: NotificationService,
    @Value("\${scheduler.check-subscription.ending-hours:24}")
    private val checkSubscriptionEnding: Long,
    private val notificationSentMap: ConcurrentHashMap<UUID, Instant> = ConcurrentHashMap<UUID, Instant>()
) {

    fun findAllSubscriptions(filter: SubscriptionFilterRequest, pageable: Pageable): Page<SubscriptionResponse> {
        return subscriptionRepository.findAllByFilter(
            pageable,
            filter.userId,
            filter.status,
            filter.serviceId,
            filter.dateFrom,
            filter.dateTo
        ).map { entity -> subscriptionMapper.toDto(entity) }
    }

    @Transactional
    fun createSubscription(request: SubscriptionRequest): SubscriptionResponse {
        var entity = subscriptionMapper.toEntity(request);
        entity = subscriptionRepository.save(entity)
        val historyUpdate = createUpdateForSubscription(entity)
        entity.history.add(historyUpdate)
        return subscriptionMapper.toDto(entity)
    }

    fun getSubscription(id: UUID): SubscriptionResponse {
        val entity = getSubscriptionEntity(id)
        return subscriptionMapper.toDto(entity)
    }

    @Transactional
    fun updateSubscription(id: UUID, request: SubscriptionRequest): SubscriptionResponse {
        val subscriptionEntity = getSubscriptionEntity(id)
        subscriptionEntity.status = request.status ?: subscriptionEntity.status
        subscriptionEntity.userId = request.userId ?: subscriptionEntity.userId
        subscriptionEntity.serviceId = request.serviceId ?: subscriptionEntity.serviceId
        subscriptionEntity.startDate = request.startDate ?: subscriptionEntity.startDate
        subscriptionEntity.duration = request.duration ?: subscriptionEntity.duration
        subscriptionEntity.endDate = (request.startDate ?: subscriptionEntity.startDate)
            .plus(request.duration ?: subscriptionEntity.duration)
        if(request.status != null || request.duration != null){
            val updateSubscription = createUpdateForSubscription(subscriptionEntity)
            subscriptionEntity.history.add(updateSubscription)
        }
        return subscriptionMapper.toDto(subscriptionEntity)
    }


    private fun getSubscriptionEntity(id: UUID): SubscriptionEntity {
        return subscriptionRepository.findById(id).orElseThrow {
            BusinessException(
                HttpStatus.NOT_FOUND,
                "Subscription not found with id: $id"
            )
        }
    }

    fun deleteSubscription(id: UUID) {
        val subscriptionEntity = getSubscriptionEntity(id)
        subscriptionRepository.delete(subscriptionEntity)
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.HOURS)
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    fun checkSubscriptionsEnd() {
        subscriptionRepository.findAllByEndDateBeforeAndStatus(Instant.now(), SubscriptionStatus.ACTIVE)
            .forEach { entity ->
                entity.status = SubscriptionStatus.EXPIRED
                entity.updatedAt = Instant.now()
                if (notificationSentMap.containsKey(entity.id)) {
                    notificationSentMap.remove(entity.id)
                }

            }
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.HOURS)
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    fun checkSubscriptionsEnding() {
        subscriptionRepository.findAllByEndDateBetweenAndStatus(
            Instant.now(),
            Instant.now().plus(checkSubscriptionEnding, ChronoUnit.HOURS),
            SubscriptionStatus.ACTIVE
        )
            .forEach { entity ->
                if (!notificationSentMap.containsKey(entity.id)) {
                    notificationService.createNotification(
                        entity.userId,
                        "Your subscription ends less than in $checkSubscriptionEnding hours"
                    )
                    notificationSentMap[entity.id] = Instant.now()
                }
            }
    }


    private fun createUpdateForSubscription(subscription: SubscriptionEntity) : SubscriptionUpdateEntity {
        val updateSubscription = SubscriptionUpdateEntity(
            subscription = subscription,
            newStatus = subscription.status,
            newDuration = subscription.duration,
            updatedAt = subscription.updatedAt
        )
        return subscriptionUpdateRepository.save(updateSubscription)
    }
}