package com.example.mvi_clean_demo.sections.blog.presentation

import android.util.Log
import com.example.mvi_clean_demo.base.BaseViewModelRepeatOnStart
import com.example.mvi_clean_demo.common.error_handling.ErrorHandler
import com.example.mvi_clean_demo.common.error_handling.ErrorHandlerFactory
import com.example.mvi_clean_demo.common.repository.ResponseState
import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState.Failure
import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState.Loading
import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState.Success
import com.example.mvi_clean_demo.common.repository.ResponseState.Idle
import com.example.mvi_clean_demo.sections.blog.data.network.repository.IBlogRepository
import com.example.mvi_clean_demo.sections.blog.domain.model.PostEntryModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel


@HiltViewModel(assistedFactory = PostsViewModel.Factory::class)
class PostsViewModel @AssistedInject constructor(
    @Assisted private val userId: Int,
    private val repository: IBlogRepository,
    errorHandlerFactory: ErrorHandlerFactory
) : BaseViewModelRepeatOnStart<PostsViewModel.Model, PostsViewModel.Event>(
    model = Model(
        isLoading = true,
        postEntriesModelsResponseState = Idle
    ),
    repeatOnStartCollectingModelStateFlow = { baseViewModel ->
        Log.d("RepeatOnStart", "ComposePosts postEntriesModelsResponseState ${baseViewModel.modelStateFlow.value.postEntriesModelsResponseState}")
        baseViewModel.sendEvent(Event.GetPostEntriesFromUser(userId))
        Log.d("RepeatOnStart", "ComposePosts sendEvent(GetPostEntriesFromUser(userId)) called")
    }
) {

    private val errorHandler: ErrorHandler = errorHandlerFactory.create(TAG)

    @AssistedFactory
    interface Factory {
        fun create(userId: Int): PostsViewModel
    }

    data class Model(
        val isLoading: Boolean,
        val postEntriesModelsResponseState: ResponseState<List<PostEntryModel>>
    )

    sealed interface Event {
        data class GetPostEntriesFromUser(val userId: Int): Event
    }

    override suspend fun processEvent(event: Event) {
        when (event) {
            is Event.GetPostEntriesFromUser -> {
                getPostEntriesFromUser(event.userId)
            }
        }
    }

    private suspend fun getPostEntriesFromUser(userId: Int) {
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
                                postEntriesModelsResponseState = state
                            )
                        }
                    }
                }
            }
    }
    companion object {
        const val TAG = "PostsViewModel"
    }
}