package com.example.mvi_clean_demo.sections.blog.data.network.models.respoonses

import com.example.mvi_clean_demo.sections.blog.domain.model.PostEntry
import com.example.mvi_clean_demo.common.api.DomainModel
import com.google.gson.annotations.SerializedName

data class PostEntryResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("userId") val userId: Int,
    @SerializedName("title") val title: String?,
    @SerializedName("body") val body: String?,
): DomainModel<PostEntry> {
    override fun toDomainModel(): PostEntry {
        return PostEntry(
            id = id,
            userId = userId,
            title = title ?: "",
            body = body ?: ""
        )
    }
}