package com.example.mvi_clean_demo.citizen.data.network.models.respoonses

import com.example.mvi_clean_demo.citizen.domain.model.CompleteFlare
import com.example.mvi_clean_demo.common.api.DtoResponse

data class CompleteFlareResponseDto(
    val eventID: String
): DtoResponse<CompleteFlare> {

    override fun toDomainModel(): CompleteFlare =
        CompleteFlare(eventID)
}