package com.example.mvi_clean_demo.sections.blog.domain.useCase


import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState
import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState.Failure
import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState.Success
import com.example.mvi_clean_demo.sections.blog.data.network.repository.IBlogRepository
import com.example.mvi_clean_demo.sections.blog.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface IGetUsersUseCase {
    suspend fun getUsers(): Flow<ActiveResponseState<List<User>>>
}

class GetUsersUseCase @Inject constructor(
    private val repository: IBlogRepository
): IGetUsersUseCase {

    override suspend fun getUsers(): Flow<ActiveResponseState<List<User>>> {
        return repository
            .getUsers()
            .map { responseState ->
                when (responseState) {
                    is Success -> {
                        lateinit var result: ActiveResponseState<List<User>>
                        runCatching {
                            responseState.data.map { user ->
                                user.copy(
                                    name = user.name.uppercase()
                                )
                            }
                        }
                            .onSuccess { users ->
                                result = Success(users)
                            }
                            .onFailure { throwable ->
                                result = Failure(throwable)
                            }
                        result
                    }
                    else -> responseState
                }
            }
    }
}