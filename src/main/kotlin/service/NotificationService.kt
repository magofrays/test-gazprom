package by.magofrays.service

import by.magofrays.entity.NotificationEntity
import by.magofrays.repository.NotificationRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class NotificationService(
    val notificationRepository: NotificationRepository
) {

    @Transactional
    fun createNotification(userId: String, message: String) {
        val notification = NotificationEntity(userId = userId, message = message)
        notificationRepository.save(notification)
    }
}