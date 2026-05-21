package by.magofrays.mapper

import by.magofrays.dto.SubscriptionRequest
import by.magofrays.dto.SubscriptionResponse
import by.magofrays.entity.SubscriptionEntity
import org.mapstruct.AfterMapping
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.ReportingPolicy
import java.time.Instant
import java.time.temporal.ChronoUnit


@Mapper(componentModel = "spring")
abstract class SubscriptionMapper {
    abstract fun toDto(entity: SubscriptionEntity): SubscriptionResponse
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    @Mapping(target = "history", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    abstract fun toEntity(dto: SubscriptionRequest): SubscriptionEntity

    @AfterMapping
    fun setEndDate(@MappingTarget entity: SubscriptionEntity, request: SubscriptionRequest) {
        entity.history = mutableListOf()
        entity.updatedAt = Instant.now()
        entity.createdAt = Instant.now()
        entity.endDate = request.startDate!!.plus(request.duration!!.toLong(), ChronoUnit.DAYS)
    }
}