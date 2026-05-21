package by.magofrays.mapper

import by.magofrays.dto.SubscriptionRequest
import by.magofrays.dto.SubscriptionResponse
import by.magofrays.entity.SubscriptionEntity
import org.mapstruct.AfterMapping
import org.mapstruct.Mapper
import org.mapstruct.MappingTarget
import java.time.temporal.ChronoUnit


@Mapper(componentModel = "spring")
abstract class SubscriptionMapper {
    abstract fun toDto(entity: SubscriptionEntity): SubscriptionResponse
    abstract fun toEntity(dto: SubscriptionRequest): SubscriptionEntity

    @AfterMapping
    fun setEndDate(@MappingTarget entity: SubscriptionEntity, request: SubscriptionRequest) {
        entity.endDate = request.startDate!!.plus(request.duration!!.toLong(), ChronoUnit.DAYS)
    }
}