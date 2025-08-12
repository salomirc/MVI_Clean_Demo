package com.example.mvi_clean_demo.citizen.data.network.models.respoonses

import com.example.mvi_clean_demo.citizen.domain.model.FlareTrigger
import com.example.mvi_clean_demo.common.api.DtoResponse

data class FlareTriggerResponseDto(
    val flareId: String,
    val eventId: String
) : DtoResponse<FlareTrigger> {

    override fun toDomainModel() : FlareTrigger =
        FlareTrigger(
            flareId = flareId,
            eventId = eventId
        )
}