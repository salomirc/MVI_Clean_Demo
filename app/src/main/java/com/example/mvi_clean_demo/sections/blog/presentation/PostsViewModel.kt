package com.example.mvi_clean_demo.sections.blog.presentation

import androidx.lifecycle.viewModelScope
import com.example.mvi_clean_demo.base.BaseViewModel
import com.example.mvi_clean_demo.common.error_handling.ErrorHandler
import com.example.mvi_clean_demo.common.error_handling.ErrorHandlerFactory
import com.example.mvi_clean_demo.common.repository.ResponseState
import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState.Failure
import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState.Loading
import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState.Success
import com.example.mvi_clean_demo.common.repository.ResponseState.Idle
import com.example.mvi_clean_demo.sections.blog.data.network.repository.IBlogRepository
import com.example.mvi_clean_demo.sections.blog.domain.model.PostEntryModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PostsViewModel @Inject constructor(
    private val repository: IBlogRepository,
    errorHandlerFactory: ErrorHandlerFactory
) : BaseViewModel<PostsViewModel.Model, PostsViewModel.Event>(
    model = Model(
        isLoading = false,
        postEntries = Idle
    )
) {

    private val errorHandler: ErrorHandler = errorHandlerFactory.create(TAG)

    data class Model(
        val isLoading: Boolean,
        val postEntries: ResponseState<List<PostEntryModel>>
    )

    sealed interface Event {
        data class GetPostEntriesFromUser(val userId: Int): Event
    }

    override fun sendEvent(event: Event) {
        when (event) {
            is Event.GetPostEntriesFromUser -> {
                getPostEntriesFromUser(event.userId)
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
        const val TAG = "PostsViewModel"
    }
}