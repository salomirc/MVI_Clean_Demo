package com.example.mvi_clean_demo.sections.blog.data.network.repository

import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState
import com.example.mvi_clean_demo.common.repository.toFlow
import com.example.mvi_clean_demo.common.retrofit.IRetrofitApiCaller
import com.example.mvi_clean_demo.sections.blog.data.network.api.BlogApi
import com.example.mvi_clean_demo.sections.blog.data.network.models.respoonses.PostEntryResponseDto
import com.example.mvi_clean_demo.sections.blog.data.network.models.respoonses.UserResponseDto
import com.example.mvi_clean_demo.sections.blog.data.room.dao.PostEntityDao
import com.example.mvi_clean_demo.sections.blog.data.room.dao.UserEntityDao
import com.example.mvi_clean_demo.sections.blog.data.room.entities.UserEntity
import com.example.mvi_clean_demo.sections.blog.domain.model.PostEntry
import com.example.mvi_clean_demo.sections.blog.domain.model.User
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface IBlogRepository {
    suspend fun getUsers(): Flow<ActiveResponseState<List<User>>>

    suspend fun getPostEntriesFromUser(
        userId: Int
    ): Flow<ActiveResponseState<List<PostEntry>>>

}

@ViewModelScoped
class BlogRepository @Inject constructor(
    private val api : BlogApi,
    private val userEntityDao: UserEntityDao,
    private val postEntityDao : PostEntityDao,
    private val apiCaller: IRetrofitApiCaller
): IBlogRepository {

    override suspend fun getUsers(): Flow<ActiveResponseState<List<User>>> {
        return flow {
            emit(ActiveResponseState.Loading)

            val localResult = runCatching {
                withContext(Dispatchers.IO) {
                    userEntityDao.getUsers()
                }.map { userEntity ->
                    userEntity.toDomainModel()
                }
            }

            localResult
                .onSuccess {
                    emit(ActiveResponseState.Success(it))
                }
                .onFailure { throwable ->
                    emit(ActiveResponseState.Failure(throwable))
                }

            emit(ActiveResponseState.Loading)

            val networkResult = apiCaller.invoke {
                    api.getUsers()
                }
                .mapCatching {
                    it.map(Mapper::mapToEntityUser)
                }
                .onSuccess { userDtoList ->
                    userDtoList.forEach { userEntity ->
                        userEntityDao.insertUser(userEntity)
                    }
                }
                .mapCatching {
                    it.map { userEntity ->
                        userEntity.toDomainModel()
                    }
                }

            networkResult
                .onSuccess {
                    emit(ActiveResponseState.Success(it))
                }
                .onFailure { throwable ->
                    emit(ActiveResponseState.Failure(throwable))
                }

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

        fun mapToEntityUser(result: UserResponseDto): UserEntity {
            return result.toEntityModel()
        }
        fun mapToPostEntry(result: PostEntryResponseDto): PostEntry {
            return result.toDomainModel()
        }
    }
}