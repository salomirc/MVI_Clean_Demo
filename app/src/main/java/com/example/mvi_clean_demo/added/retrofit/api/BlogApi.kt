package com.example.mvi_clean_demo.added.retrofit.api

import com.example.mvi_clean_demo.added.retrofit.dto.PostEntryDto
import retrofit2.http.GET
import retrofit2.http.Query

interface BlogApi {

    @GET("posts")
    suspend fun getPostsFromUser(
        @Query("userId") userId: Int
    ): List<PostEntryDto>
}