package com.example.mvi_clean_demo.citizen.presentation

import androidx.lifecycle.viewModelScope
import com.example.mvi_clean_demo.R
import com.example.mvi_clean_demo.citizen.data.network.repository.IBlogRepository
import com.example.mvi_clean_demo.citizen.domain.model.PostEntry
import com.example.mvi_clean_demo.citizen.domain.model.User
import com.example.mvi_clean_demo.citizen.domain.useCase.IGetUsersUseCase
import com.example.mvi_clean_demo.common.error_handling.ErrorHandler
import com.example.mvi_clean_demo.common.error_handling.ErrorHandlerFactory
import com.example.mvi_clean_demo.common.repository.ResponseState
import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState.Failure
import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState.Loading
import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState.Success
import com.example.mvi_clean_demo.common.repository.ResponseState.Idle
import com.example.mvi_clean_demo.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UsersViewModel @Inject constructor(
    private val repository: IBlogRepository,
    private val getUsersUseCase: IGetUsersUseCase,
    errorHandlerFactory: ErrorHandlerFactory
) : BaseViewModel<UsersViewModel.Model, UsersViewModel.Event>(
    model = Model(
        isLoading = false,
        users = Idle,
        postEntries = Idle
    )
) {

    private val errorHandler: ErrorHandler = errorHandlerFactory.create(TAG)
    data class Model(
        val isLoading: Boolean,
        val users: ResponseState<List<User>>,
        val postEntries: ResponseState<List<PostEntry>>
    )

    sealed interface Event {
        data object GetUsers: Event
        data class GetPostEntriesFromUser(val userId: Int): Event
    }

    override fun sendEvent(event: Event) {
        when (event) {
            is Event.GetUsers -> {
                getUsers()
            }
            is Event.GetPostEntriesFromUser -> {
                getPostEntriesFromUser(event.userId)
            }
        }
    }

    private fun getUsers() {
        viewModelScope.launch {
            getUsersUseCase
                .getUsers()
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
                                    users = state
                                )
                            }
                        }
                    }
                }
        }
    }
    private fun getPostEntriesFromUser(userId: Int) {
        viewModelScope.launch {
            repository
                .getPostEntriesFromUser(userId = userId)
                .collect { state ->
                    when (state) {
                        is Loading -> {
                            updateModelState { model -> model.copy(isLoading = true) }
                        }
                        is Failure -> {
                            updateModelState { model -> model.copy(isLoading = false)}
                            //Default Error Handler
                            errorHandler.handleError(state.throwable)
                        }
                        is Success -> {
                            updateModelState { model ->
                                model.copy(
                                    isLoading = false,
                                    postEntries = state
                                )
                            }
                        }
                    }
                }

        }
    }
    companion object {
        const val TAG = "UsersViewModel"
    }
}