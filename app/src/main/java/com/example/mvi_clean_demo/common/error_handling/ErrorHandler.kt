package com.example.mvi_clean_demo.common.error_handling

import android.util.Log
import com.example.mvi_clean_demo.R
import com.example.mvi_clean_demo.common.api.ApiException
import com.example.mvi_clean_demo.common.retrofit.NetworkException
import com.example.mvi_clean_demo.common.retrofit.NoNetworkException
import com.example.mvi_clean_demo.common.retrofit.SerializationException
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.IOException

@AssistedFactory
interface ErrorHandlerFactory {
    fun create(viewModelName: String): ErrorHandler
}

open class ErrorHandler @AssistedInject constructor(
    @Assisted private var viewModelName: String,
    val broadcastService: IErrorHandlerBroadcastService
) {

    fun handleError(t: Throwable) {
        defaultErrorHandler().invoke(t)
    }

    open fun defaultErrorHandler(
        onNoNetworkException: (NoNetworkException) -> Unit = defaultNoNetworkExceptionHandler(),
        onApiException: (ApiException) -> Unit = defaultApiExceptionHandler(),
        onNetworkException: (NetworkException) -> Unit = defaultNetworkExceptionHandler(),
        onSerializationException: (SerializationException) -> Unit = defaultSerializationExceptionHandler(),
        onIOException: (IOException) -> Unit = defaultIOExceptionHandler(),
        onOtherException: (Throwable) -> Unit = defaultOtherExceptionHandler(),
    ): (Throwable) -> Unit = { throwable ->

        Log.d(ERROR_HANDLER_TAG, "ErrorHandler $viewModelName exception: $throwable")

        // Order is very important! From specific to general.
        when (throwable) {
            is NoNetworkException -> onNoNetworkException(throwable)
            is ApiException -> onApiException(throwable)
            is NetworkException -> onNetworkException(throwable)
            is SerializationException -> onSerializationException(throwable)
            is IOException -> onIOException(throwable)
            else -> onOtherException(throwable)
        }
    }

    private fun defaultNoNetworkExceptionHandler(): (NoNetworkException) -> Unit = { t ->
        broadcastService.setErrorMessage(R.string.error_no_internet_connection)
    }

    private fun defaultNetworkExceptionHandler(): (NetworkException) -> Unit = {
        setDefaultMessage()
    }

    private fun defaultSerializationExceptionHandler(): (Throwable) -> Unit = {
        setDefaultMessage()
    }

    private fun defaultIOExceptionHandler(): (IOException) -> Unit = {
        setDefaultMessage()
    }

    private fun defaultOtherExceptionHandler(): (Throwable) -> Unit = {
        setDefaultMessage()
    }

    open fun defaultApiExceptionHandler(
        on401: (ApiException) -> Unit = defaultHttp4xxHandler(),
        on403Forbidden: (ApiException) -> Unit = defaultHttp403ForbiddenHandler(),
        on409: (ApiException) -> Unit = defaultHttp4xxHandler(),
        on4xx: (ApiException) -> Unit = defaultHttp4xxHandler(),
        on5xx: (ApiException) -> Unit = defaultHttp5xxHandler(),
    ): (ApiException) -> Unit = { apiException ->

        when (apiException.code) {
            401 -> on401(apiException)
            403 -> on403Forbidden(apiException)
            409 -> on409(apiException)
            400, 402, in (404..499) -> on4xx(apiException)
            else -> on5xx(apiException)
        }
    }

    private fun defaultHttp403ForbiddenHandler(): (ApiException) -> Unit = {
        setDefaultMessage()
    }

    private fun defaultHttp4xxHandler(): (ApiException) -> Unit = {
        setDefaultMessage()
    }

    private fun defaultHttp5xxHandler(): (ApiException) -> Unit = {
        setDefaultMessage()
    }

    private fun setDefaultMessage() {
        broadcastService.setErrorMessage(R.string.error_generic_network_issue)
    }

    companion object {
        const val ERROR_HANDLER_TAG = "ErrorHandlerLog"
    }

}