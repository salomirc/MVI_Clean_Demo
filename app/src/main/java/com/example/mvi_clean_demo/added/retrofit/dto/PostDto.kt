package com.example.mvi_clean_demo.added.retrofit.dto

import com.google.gson.annotations.SerializedName

data class PostEntryDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("userId") val userId: Int?,
    @SerializedName("title") val title: String?,
    @SerializedName("body") val body: String?,
)








