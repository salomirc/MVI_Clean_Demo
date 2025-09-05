package com.example.mvi_clean_demo.sections.blog.presentation

import com.example.mvi_clean_demo.common.error_handling.ErrorHandler
import com.example.mvi_clean_demo.common.error_handling.ErrorHandlerBroadcastService
import com.example.mvi_clean_demo.common.error_handling.ErrorHandlerFactory
import com.example.mvi_clean_demo.common.repository.ResponseState
import com.example.mvi_clean_demo.sections.blog.domain.useCase.IGetUsersUseCase
import com.example.mvi_clean_demo.sections.blog.presentation.preview_sample_data.UsersSampleData
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test

@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
class UsersViewModelTest {
    private val getUsersUseCase = mockk<IGetUsersUseCase>()
    private val errorHandlerFactory = mockk<ErrorHandlerFactory>()
    private lateinit var viewModel: UsersViewModel

    private val sampleUsers = UsersSampleData.users


    @Before
    fun setUp() {
        clearMocks(getUsersUseCase, errorHandlerFactory)
        coEvery { errorHandlerFactory.create(any()) } returns ErrorHandler(UsersViewModel.TAG,
            ErrorHandlerBroadcastService()
        )
        viewModel =  UsersViewModel(getUsersUseCase, errorHandlerFactory)
    }

    @Test
    fun getUsers_WhenSuccess_ShouldUpdateState() {
        //Arrange
        val userCardModelsResponseState = ResponseState.ActiveResponseState.Success(sampleUsers)
        coEvery { getUsersUseCase.getUsers() } returns flow {
            emit(userCardModelsResponseState)
        }
        //Act
        runBlocking {
            viewModel.getUsers()
        }
        val result = viewModel.modelStateFlow.value.userCardModelsResponseState as? ResponseState.ActiveResponseState.Success
        //Assert
        coVerify { getUsersUseCase.getUsers() }
        Assertions.assertThat(result?.data?.map { it.userModel }).isEqualTo(sampleUsers)
    }

    @Test
    fun add_whenCalled_returnTheSumOfArguments() {
        val result = 3
        Assertions.assertThat(result).isEqualTo(3)
    }

}
