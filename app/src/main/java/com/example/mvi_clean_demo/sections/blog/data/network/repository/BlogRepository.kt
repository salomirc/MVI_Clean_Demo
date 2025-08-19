package com.example.mvi_clean_demo.sections.blog.data.network.repository

import com.example.mvi_clean_demo.common.repository.DataSourcePattern
import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
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
        return DataSourcePattern.dualPattern(
            networkResult = {
                apiCaller.invoke {
                    delay(1000)
                    api.getUsers()
                }
                    .mapCatching { userResponseDtos ->
                        userResponseDtos.map(Mapper::mapToEntityUser)
                    }
                    .onSuccess { userEntities ->
                        runCatching {
                            withContext(Dispatchers.IO) {
                                userEntityDao.deleteAllUsers()
                                userEntities.forEach { userEntity ->
                                    userEntityDao.insertUser(userEntity)
                                }
                            }
                        }.onFailure {
                            emit(ActiveResponseState.Failure(it))
                        }
                    }
                    .mapCatching { userEntities ->
                        userEntities.map { userEntity ->
                            userEntity.toDomainModel()
                        }
                    }
            },
            localResult = {
                runCatching {
                    withContext(Dispatchers.IO) {
                        userEntityDao.getUsers()
                    }.map { userEntity ->
                        userEntity.toDomainModel()
                    }
                }
            }
        )
    }

    override suspend fun getPostEntriesFromUser(
        userId: Int
    ): Flow<ActiveResponseState<List<PostEntry>>> {
        return DataSourcePattern.singlePattern(
            result = {
                apiCaller.invoke {
                    api.getPostsFromUser(userId = userId)
                }
                    .mapCatching {
                        it.map(Mapper::mapToPostEntry)
                    }

            }
        )
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