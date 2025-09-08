package com.example.mvi_clean_demo.sections.blog.presentation

import androidx.compose.runtime.Immutable
import com.example.mvi_clean_demo.R
import com.example.mvi_clean_demo.base.BaseViewModel
import com.example.mvi_clean_demo.common.error_handling.ErrorHandler
import com.example.mvi_clean_demo.common.error_handling.ErrorHandlerFactory
import com.example.mvi_clean_demo.common.repository.ResponseState
import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState.Failure
import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState.Loading
import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState.Success
import com.example.mvi_clean_demo.common.repository.ResponseState.Idle
import com.example.mvi_clean_demo.common.repository.activeResponseStateWrapper
import com.example.mvi_clean_demo.common.repository.responseStateWrapper
import com.example.mvi_clean_demo.sections.blog.domain.useCase.IGetUsersUseCase
import com.example.mvi_clean_demo.sections.blog.presentation.model.UserCardModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class UsersViewModel @Inject constructor(
    private val getUsersUseCase: IGetUsersUseCase,
    errorHandlerFactory: ErrorHandlerFactory
) : BaseViewModel<UsersViewModel.Model, UsersViewModel.Event>(
    model = Model(
        isLoading = true,
        userCardModelsResponseState = Idle
    )
) {

    private val errorHandler: ErrorHandler = errorHandlerFactory.create(TAG)

    data class Model(
        val isLoading: Boolean,
        val userCardModelsResponseState: ResponseState<List<UserCardModel>>
    )

    @Immutable
    sealed interface Event {
        data object GetUsers: Event
        data class UpdateUserCardModel(val isExpanded: Boolean, val id: Int): Event
    }

    override suspend fun processEvent(event: Event) {
        when (event) {
            is Event.GetUsers -> {
                getUsers()
            }
            is Event.UpdateUserCardModel -> {
                updateUserCardModel(event.isExpanded, event.id)
            }
        }
    }

    private suspend fun getUsers() {
        getUsersUseCase
            .getUsers()
            .map { state ->
                withContext(Dispatchers.Default) {
                    state.activeResponseStateWrapper { users ->
                        users.map { user -> user.toUIModel() }
                    }
                }
            }
            .collect { state ->
                when (state) {
                    is Loading -> {
                        updateModelState { model -> model.copy(isLoading = true) }
                    }
                    is Failure -> {
                        updateModelState { model -> model.copy(isLoading = false) }
                        //Custom Error Handler
                        errorHandler.apply {
                            defaultErrorHandler(
                                onApiException = defaultApiExceptionHandler(
                                    on401 = {
                                        broadcastService.setErrorMessage(R.string.error_access_denied)
                                    },
                                    on409 = {
                                        broadcastService.setErrorMessage(R.string.error_invalid_credentials)
                                    }

                                )
                            ).invoke(state.throwable)
                        }
                    }
                    is Success -> {
                        updateModelState { model ->
                            model.copy(
                                isLoading = false,
                                userCardModelsResponseState = state
                            )
                        }
                    }
                }
            }
    }
    private suspend fun updateUserCardModel(expanded: Boolean, id: Int) {
        updateModelStateSuspend { model ->
            model.copy(
                userCardModelsResponseState = withContext(Dispatchers.Default) {
                    model.userCardModelsResponseState.responseStateWrapper { userCardModels ->
                        userCardModels.apply {
                            this.find { it.userModel.id == id }?.isExpanded = expanded
                        }
                    }
                }
            )
        }
    }
    companion object {
        const val TAG = "UsersViewModel"
    }
}