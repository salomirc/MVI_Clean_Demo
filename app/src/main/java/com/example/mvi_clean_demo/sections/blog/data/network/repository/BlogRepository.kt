package com.example.mvi_clean_demo.sections.blog.data.network.repository

import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState
import com.example.mvi_clean_demo.common.repository.toFlow
import com.example.mvi_clean_demo.common.retrofit.IRetrofitApiCaller
import com.example.mvi_clean_demo.sections.blog.data.network.api.BlogApi
import com.example.mvi_clean_demo.sections.blog.data.network.models.respoonses.PostEntryResponseDto
import com.example.mvi_clean_demo.sections.blog.data.network.models.respoonses.UserResponseDto
import com.example.mvi_clean_demo.sections.blog.domain.model.PostEntry
import com.example.mvi_clean_demo.sections.blog.domain.model.User
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface IBlogRepository {
    suspend fun getUsers(): Result<List<User>>

    suspend fun getPostEntriesFromUser(
        userId: Int
    ): Flow<ActiveResponseState<List<PostEntry>>>

}

@ViewModelScoped
class BlogRepository @Inject constructor(
    private val api : BlogApi,
    private val apiCaller: IRetrofitApiCaller
): IBlogRepository {

    override suspend fun getUsers(): Result<List<User>> {
        return apiCaller.invoke {
            api.getUsers()
        }.mapCatching {
            it.map(Mapper::mapToUser)
        }
    }

    override suspend fun getPostEntriesFromUser(
        userId: Int
    ): Flow<ActiveResponseState<List<PostEntry>>> {
        return apiCaller.invoke {
            api.getPostsFromUser(userId = userId)
        }.mapCatching {
            it.map(Mapper::mapToPostEntry)
        }.toFlow()
    }

    private object Mapper {
        fun mapToUser(result: UserResponseDto): User {
            return result.toDomainModel()
        }
        fun mapToPostEntry(result: PostEntryResponseDto): PostEntry {
            return result.toDomainModel()
        }
    }
}