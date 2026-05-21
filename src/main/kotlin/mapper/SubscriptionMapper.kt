package by.magofrays.mapper

import by.magofrays.dto.SubscriptionRequest
import by.magofrays.dto.SubscriptionResponse
import by.magofrays.entity.SubscriptionEntity
import org.mapstruct.AfterMapping
import org.mapstruct.Mapper
import org.mapstruct.MappingTarget

@Mapper(componentModel = "spring")
interface SubscriptionMapper {
    fun toDto(entity: SubscriptionEntity) : SubscriptionResponse
    fun toEntity(dto: SubscriptionRequest) : SubscriptionEntity

    @AfterMapping
    fun setEndDate(@MappingTarget entity: SubscriptionEntity, request: SubscriptionRequest) {
        entity.endDate = request.startDate!!.plus(request.duration)
    }
}