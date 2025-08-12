package com.example.mvi_clean_demo.citizen.domain.useCase


import com.example.mvi_clean_demo.citizen.data.network.repository.IBlogRepository
import com.example.mvi_clean_demo.citizen.domain.model.User
import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState
import com.example.mvi_clean_demo.common.repository.toFlow
import kotlinx.coroutines.flow.Flow
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
            .mapCatching {
                it.reversed()
            }
            .toFlow()
    }

}