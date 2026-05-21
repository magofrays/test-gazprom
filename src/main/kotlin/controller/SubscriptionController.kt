package by.magofrays.controller

import by.magofrays.dto.SubscriptionFilterRequest
import by.magofrays.dto.SubscriptionRequest
import by.magofrays.dto.SubscriptionResponse
import by.magofrays.service.SubscriptionService
import by.magofrays.validation.CreateGroup
import by.magofrays.validation.UpdateGroup
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/subscription")
class SubscriptionController(
    val subscriptionService: SubscriptionService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping
    fun createSubscription(
        @Validated(CreateGroup::class) @RequestBody request: SubscriptionRequest
    ): ResponseEntity<SubscriptionResponse> {
        log.info("REST request to create Subscription: {}", request)
        return ResponseEntity.status(HttpStatus.CREATED).body(subscriptionService.createSubscription(request))
    }

    @GetMapping
    fun getSubscriptions(
        @PageableDefault(size = 20) pageable: Pageable,
        @ModelAttribute request: SubscriptionFilterRequest
    ): ResponseEntity<Page<SubscriptionResponse>> {
        log.info("REST request to get Subscriptions with filter: {}", request)
        return ResponseEntity.ok(subscriptionService.findAllSubscriptions(request, pageable))
    }

    @GetMapping("{id}")
    fun getSubscription(@PathVariable id: UUID): ResponseEntity<SubscriptionResponse> {
        log.info("REST request to get Subscription by id: {}", id)
        return ResponseEntity.ok(subscriptionService.getSubscription(id))
    }

    @PutMapping("{id}")
    fun updateSubscription(
        @PathVariable id: UUID,
        @Validated(UpdateGroup::class) @RequestBody request: SubscriptionRequest
    ): ResponseEntity<SubscriptionResponse> {
        log.info("REST request to update Subscription {}: {}", id, request)
        return ResponseEntity.ok(subscriptionService.updateSubscription(id, request))
    }

    @DeleteMapping("{id}")
    fun deleteSubscription(
        @PathVariable id: UUID
    ) : ResponseEntity<Void> {
        log.info("REST request to delete Subscription by id: {}", id)
        subscriptionService.deleteSubscription(id)
        return ResponseEntity.noContent().build()
    }
}