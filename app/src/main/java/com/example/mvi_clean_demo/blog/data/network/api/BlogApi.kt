package com.example.mvi_clean_demo.blog.data.network.api

import com.example.mvi_clean_demo.blog.data.network.models.respoonses.PostEntryResponseDto
import com.example.mvi_clean_demo.blog.data.network.models.respoonses.UserResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface BlogApi {

    @GET("/users")
    suspend fun getUsers(): Response<List<UserResponseDto>>

    @GET("/posts")
    suspend fun getPostsFromUser(@Query("userId") userId: Int): Response<List<PostEntryResponseDto>>
}