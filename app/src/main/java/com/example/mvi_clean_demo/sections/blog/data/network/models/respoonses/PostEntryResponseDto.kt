package com.example.mvi_clean_demo.sections.blog.data.network.models.respoonses

import com.example.mvi_clean_demo.common.api.EntityModel
import com.example.mvi_clean_demo.sections.blog.data.room.entities.PostEntity
import com.google.gson.annotations.SerializedName

data class PostEntryResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("userId") val userId: Int,
    @SerializedName("title") val title: String?,
    @SerializedName("body") val body: String?,
): EntityModel<PostEntity> {

    override fun toEntityModel(): PostEntity {
        return PostEntity(
            id = id,
            userId = userId,
            title = title ?: "",
            body = body ?: ""
        )
    }
}