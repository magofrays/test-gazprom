package by.magofrays.service

import by.magofrays.dto.SubscriptionFilterRequest
import by.magofrays.dto.SubscriptionRequest
import by.magofrays.dto.SubscriptionResponse
import by.magofrays.dto.SubscriptionStatus
import by.magofrays.entity.SubscriptionEntity
import by.magofrays.exception.BusinessException
import by.magofrays.mapper.SubscriptionMapper
import by.magofrays.mapper.SubscriptionMapperImpl
import by.magofrays.repository.SubscriptionRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [
    SubscriptionMapperImpl::class,
    SubscriptionService::class
])
class SubscriptionServiceTest {

    @MockitoBean
    private lateinit var subscriptionRepository: SubscriptionRepository

    @MockitoBean
    private lateinit var subscriptionMapper: SubscriptionMapper

    @MockitoBean
    private lateinit var notificationService: NotificationService

    @Autowired
    private lateinit var subscriptionService: SubscriptionService

    private val now = Instant.now()
    private val testId = UUID.randomUUID()

    private fun createTestEntity(
        id: UUID = testId,
        userId: String = "user123",
        serviceId: String = "service456",
        startDate: Instant = now,
        endDate: Instant = now.plus(30, ChronoUnit.DAYS),
        status: SubscriptionStatus = SubscriptionStatus.ACTIVE,
        duration: Int = 30
    ) = SubscriptionEntity(
        id = id,
        userId = userId,
        serviceId = serviceId,
        startDate = startDate,
        endDate = endDate,
        status = status,
        duration = duration,
        history = mutableListOf(),
        createdAt = now,
        updatedAt = now
    )

    private fun createTestResponse(
        id: UUID = testId,
        userId: String = "user123",
        serviceId: String = "service456",
        status: SubscriptionStatus = SubscriptionStatus.ACTIVE,
        startDate: Instant = now,
        endDate: Instant = now.plus(30, ChronoUnit.DAYS),
        duration: Int = 30
    ) = SubscriptionResponse(
        id = id,
        userId = userId,
        serviceId = serviceId,
        status = status,
        startDate = startDate,
        endDate = endDate,
        duration = duration,
        createdAt = now,
        updatedAt = now
    )

    @Test
    fun `should find all subscriptions with pagination`() {
        val filter = SubscriptionFilterRequest(
            userId = "user123",
            status = SubscriptionStatus.ACTIVE,
            serviceId = null,
            dateFrom = null,
            dateTo = null
        )
        val pageable = PageRequest.of(0, 20, Sort.by("startDate").descending())
        val entity = createTestEntity()
        val response = createTestResponse()
        val page: Page<SubscriptionEntity> = PageImpl(listOf(entity), pageable, 1)

        `when`(subscriptionRepository.findAllByFilter(
            filter.userId, filter.status, filter.serviceId,
            filter.dateFrom, filter.dateTo, pageable
        )).thenReturn(page)
        `when`(subscriptionMapper.toDto(entity)).thenReturn(response)

        val result = subscriptionService.findAllSubscriptions(filter, pageable)

        assertNotNull(result)
        assertEquals(1, result.totalElements)
        assertEquals(response, result.content[0])
    }

    @Test
    fun `should create subscription successfully`() {
        val request = SubscriptionRequest(
            userId = "user123",
            serviceId = "service456",
            startDate = now,
            duration = 30,
            status = null
        )
        val entity = createTestEntity()
        val response = createTestResponse()

        `when`(subscriptionMapper.toEntity(request)).thenReturn(entity)
        `when`(subscriptionRepository.save(entity)).thenReturn(entity)
        `when`(subscriptionMapper.toDto(entity)).thenReturn(response)

        val result = subscriptionService.createSubscription(request)

        assertNotNull(result)
        assertEquals(response.id, result.id)
        verify(subscriptionRepository).save(entity)
    }

    @Test
    fun `should add history when creating subscription`() {
        val request = SubscriptionRequest(
            userId = "user123",
            serviceId = "service456",
            startDate = now,
            duration = 30,
            status = null
        )
        val entity = createTestEntity()
        val response = createTestResponse()

        `when`(subscriptionMapper.toEntity(request)).thenReturn(entity)
        `when`(subscriptionRepository.save(entity)).thenReturn(entity)
        `when`(subscriptionMapper.toDto(entity)).thenReturn(response)

        subscriptionService.createSubscription(request)

        assertEquals(1, entity.history?.size)
    }

    @Test
    fun `should get subscription by id`() {
        val id = UUID.randomUUID()
        val entity = createTestEntity(id = id)
        val response = createTestResponse(id = id)

        `when`(subscriptionRepository.findById(id)).thenReturn(Optional.of(entity))
        `when`(subscriptionMapper.toDto(entity)).thenReturn(response)

        val result = subscriptionService.getSubscription(id)

        assertEquals(id, result.id)
    }

    @Test
    fun `should throw BusinessException when subscription not found`() {
        val id = UUID.randomUUID()
        `when`(subscriptionRepository.findById(id)).thenReturn(Optional.empty())

        val exception = assertThrows(BusinessException::class.java) {
            subscriptionService.getSubscription(id)
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.status)
    }

    @Test
    fun `should update subscription fields`() {
        val id = UUID.randomUUID()
        val entity = createTestEntity(id = id)
        val request = SubscriptionRequest(
            userId = "newUser",
            status = SubscriptionStatus.FROZEN,
            serviceId = null,
            startDate = null,
            duration = null
        )
        val response = createTestResponse(id = id, userId = "newUser", status = SubscriptionStatus.FROZEN)

        `when`(subscriptionRepository.findById(id)).thenReturn(Optional.of(entity))
        `when`(subscriptionMapper.toDto(entity)).thenReturn(response)

        subscriptionService.updateSubscription(id, request)

        assertEquals(SubscriptionStatus.FROZEN, entity.status)
        assertEquals("newUser", entity.userId)
    }

    @Test
    fun `should recalculate endDate when duration changes`() {
        val id = UUID.randomUUID()
        val startDate = Instant.now()
        val entity = createTestEntity(id = id, startDate = startDate, duration = 30)
        val request = SubscriptionRequest(
            duration = 60,
            userId = null,
            serviceId = null,
            startDate = null,
            status = null
        )
        val response = createTestResponse(id = id, duration = 60)

        `when`(subscriptionRepository.findById(id)).thenReturn(Optional.of(entity))
        `when`(subscriptionMapper.toDto(entity)).thenReturn(response)

        subscriptionService.updateSubscription(id, request)

        assertEquals(startDate.plus(60, ChronoUnit.DAYS), entity.endDate)
    }

    @Test
    fun `should add history when status changes in update`() {
        val id = UUID.randomUUID()
        val entity = createTestEntity(id = id)
        val request = SubscriptionRequest(
            status = SubscriptionStatus.FROZEN,
            userId = null,
            serviceId = null,
            startDate = null,
            duration = null
        )
        val response = createTestResponse(id = id, status = SubscriptionStatus.FROZEN)

        `when`(subscriptionRepository.findById(id)).thenReturn(Optional.of(entity))
        `when`(subscriptionMapper.toDto(entity)).thenReturn(response)

        subscriptionService.updateSubscription(id, request)

        assertTrue(entity.history!!.isNotEmpty())
    }

    @Test
    fun `should delete subscription successfully`() {
        val id = UUID.randomUUID()
        val entity = createTestEntity(id = id)

        `when`(subscriptionRepository.findById(id)).thenReturn(Optional.of(entity))

        subscriptionService.deleteSubscription(id)

        verify(subscriptionRepository).delete(entity)
    }

    @Test
    fun `should throw exception when deleting non-existent subscription`() {
        val id = UUID.randomUUID()
        `when`(subscriptionRepository.findById(id)).thenReturn(Optional.empty())

        assertThrows(BusinessException::class.java) {
            subscriptionService.deleteSubscription(id)
        }
    }
}