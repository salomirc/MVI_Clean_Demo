package com.example.mvi_clean_demo.sections.blog.domain.useCase


import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState
import com.example.mvi_clean_demo.common.repository.activeResponseStateWrapper
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
                responseState.activeResponseStateWrapper { users ->
                    users.map { user ->
                        user.copy(
                            name = user.name.uppercase()
                        )
                    }
                }
            }
    }
}