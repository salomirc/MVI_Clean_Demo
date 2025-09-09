package com.example.mvi_clean_demo.sections.blog.presentation

import com.example.mvi_clean_demo.common.error_handling.ErrorHandler
import com.example.mvi_clean_demo.common.error_handling.ErrorHandlerFactory
import com.example.mvi_clean_demo.common.error_handling.IErrorHandlerBroadcastService
import com.example.mvi_clean_demo.common.error_handling.Logger
import com.example.mvi_clean_demo.common.repository.ResponseState
import com.example.mvi_clean_demo.sections.blog.domain.useCase.IGetUsersUseCase
import com.example.mvi_clean_demo.sections.blog.presentation.preview_sample_data.UsersSampleData
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test

class UsersViewModelTest {
    private val getUsersUseCase = mockk<IGetUsersUseCase>()
    private val errorHandlerFactory = mockk<ErrorHandlerFactory>()

    // relaxed = true, allows creation with no specific behaviour. Unstubbed methods will not throw.
    private val logger = mockk<Logger>(relaxed = true)
    private val errorHandlerBroadcastService = mockk<IErrorHandlerBroadcastService>(relaxed = true)
    private lateinit var viewModel: UsersViewModel

    private val sampleUsers = UsersSampleData.users
    private val sampleModels = UsersSampleData.models


    @Before
    fun setUp() {
        clearMocks(getUsersUseCase, errorHandlerFactory, logger, errorHandlerBroadcastService)
        every { errorHandlerFactory.create(any(), any()) } returns ErrorHandler(
            viewModelName = UsersViewModel.TAG,
            logger = logger,
            broadcastService = errorHandlerBroadcastService,
        )
        viewModel =  UsersViewModel(getUsersUseCase, errorHandlerFactory)
    }

    @Test
    fun event_GetUsers_WhenSuccess_ShouldUpdateState() {
        //Arrange
        val userCardModelsResponseState = ResponseState.ActiveResponseState.Success(sampleUsers)
        coEvery { getUsersUseCase.getUsers() } returns flow {
            emit(userCardModelsResponseState)
        }
        //Act
        runBlocking {
            viewModel.processEvent(UsersViewModel.Event.GetUsers)
        }
        val result = viewModel.modelStateFlow.value.userCardModelsResponseState as? ResponseState.ActiveResponseState.Success
        //Assert
        coVerify { getUsersUseCase.getUsers() }
        Assertions.assertThat(result?.data?.map { it.userModel }).isEqualTo(sampleUsers)
    }

    @Test
    fun event_GetUsers_WhenFailure_ShouldUpdateState() {
        //Arrange
        val userCardModelsResponseState = ResponseState.ActiveResponseState.Failure(Exception("Unit Test Exception!"))
        coEvery { getUsersUseCase.getUsers() } returns flow {
            emit(userCardModelsResponseState)
        }
        //Act
        runBlocking {
            viewModel.processEvent(UsersViewModel.Event.GetUsers)
        }
        val result = viewModel.modelStateFlow.value.userCardModelsResponseState as? ResponseState.ActiveResponseState.Failure
        //Assert
        coVerify { getUsersUseCase.getUsers() }
        Assertions.assertThat(result?.throwable).isEqualTo(userCardModelsResponseState.throwable)
    }

    @Test
    fun event_UpdateUserCardModel_WhenCalled_ShouldUpdateState() {
        //Arrange
        val userCardModelsResponseState = ResponseState.ActiveResponseState.Success(sampleModels)
        viewModel.updateModelState { model ->
            model.copy(
                isLoading = false,
                userCardModelsResponseState = userCardModelsResponseState
            )
        }
        //Act
        runBlocking {
            viewModel.processEvent(UsersViewModel.Event.UpdateUserCardModel(isExpanded = false, id = 1))
        }
        val result = viewModel.modelStateFlow.value.userCardModelsResponseState as? ResponseState.ActiveResponseState.Success
        //Assert
        Assertions.assertThat(result?.data?.find { it.userModel.id == 1 }?.isExpanded).isEqualTo(false)
    }
}
