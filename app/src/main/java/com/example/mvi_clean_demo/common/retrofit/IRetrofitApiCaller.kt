package com.example.mvi_clean_demo.common.retrofit

import android.util.Log
import com.example.mvi_clean_demo.common.api.ApiException
import com.example.mvi_clean_demo.common.api.ApiSuccess
import com.example.mvi_clean_demo.common.connectivity.IConnectivityChecker
import com.google.gson.JsonParseException
import com.google.gson.stream.MalformedJsonException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * An interface for making API calls for services returning a Retrofit [Response].
 * It is used to abstract away the implementation of the API call and to wrap the call in order to provide unified
 * (or even centralized, if desired) error handling.
 */
interface IRetrofitApiCaller {

    /**
     * Converts a [Response] to [Result] containing the body of type [T].
     */
    suspend operator fun <T: Any> invoke(
        callBlock: suspend () -> Response<T>
    ): Result<T>

    /**
     * Converts a [Response] to [Result] containing the body of type [Unit].
     */
    suspend fun invokeUnit(
        callBlock: suspend () -> Response<Unit>
    ): Result<Unit>

    /**
     * Converts a [Response] to [Result] containing the body of type [T] nullable.
     */
    suspend fun <T> invokeNullable(
        callBlock: suspend () -> Response<T?>
    ): Result<T?>

    /**
     * Converts a Retrofit [Response] to [ApiSuccess] containing the body of type [T] and the HTTP status code.
     */
    suspend fun <T: Any> raw(
        callBlock: suspend () -> Response<T>
    ): Result<ApiSuccess<T>>

    /**
     * Converts a Retrofit [Response] to [ApiSuccess] containing the body of type [Unit] and the HTTP status code.
     */
    suspend fun rawUnit(
        callBlock: suspend () -> Response<Unit>
    ): Result<ApiSuccess<Unit>>

    /**
     * Converts a Retrofit [Response] to [ApiSuccess] containing the body of type [T] nullable and the HTTP status code.
     */
    suspend fun <T> rawNullable(
        callBlock: suspend () -> Response<T?>
    ): Result<ApiSuccess<T?>>
}

/**
 * Creates an [IRetrofitApiCaller] that can be used to make calls for API services created with Retrofit.
 * All exceptions are caught and propagated to [Result].
 */
@Singleton
class RetrofitApiCaller @Inject constructor(
    private val connectivity: IConnectivityChecker,
): IRetrofitApiCaller {
        /**
         * Converts a [Response] to [Result] while catching all exceptions.
         *
         * @throws NoNetworkException in case of no connectivity
         */
        override suspend fun <T: Any> invoke(callBlock: suspend () -> Response<T>): Result<T> =
            raw(callBlock).mapCatching { it.body }

        /**
         * Converts a [Response] to [Result] while catching all exceptions.
         *
         * @throws NoNetworkException in case of no connectivity
         */
        override suspend fun invokeUnit(callBlock: suspend () -> Response<Unit>): Result<Unit> =
            rawUnit(callBlock).mapCatching { it.body }

        /**
         * Converts a [Response] to [Result] while catching all exceptions.
         *
         * @throws NoNetworkException in case of no connectivity
         */
        override suspend fun <T> invokeNullable(callBlock: suspend () -> Response<T?>): Result<T?> =
            rawNullable(callBlock).mapCatching { it.body }

        /**
         * Converts a [Response] to [Result] while catching all exceptions. The response body is wrapped in [ApiSuccess]
         * wrapper. Use this method when raw HTTP status code, headers, etc. are needed along with the typed body.
         *
         * @throws NoNetworkException in case of no connectivity
         */
        override suspend fun <T: Any> raw(callBlock: suspend () -> Response<T>): Result<ApiSuccess<T>> {
            return networkCallHandling(callBlock = callBlock) {
                it ?: throw NoResponseBodyException(NOT_NULL_BODY_EXPECTED_MESSAGE) }
        }

        /**
         * Converts a [Response] to [Result] while catching all exceptions. The response body is wrapped in [ApiSuccess]
         * wrapper. Use this method when raw HTTP status code, headers, etc. are needed along with the typed body.
         *
         * @throws NoNetworkException in case of no connectivity
         */
        override suspend fun rawUnit(callBlock: suspend () -> Response<Unit>): Result<ApiSuccess<Unit>> {
            return networkCallHandling(callBlock = callBlock) { it ?: Unit }
        }

        /**
         * Converts a [Response] to [Result] while catching all exceptions. The response body is wrapped in [ApiSuccess]
         * wrapper. Use this method when raw HTTP status code, headers, etc. are needed along with the typed body.
         *
         * @throws NoNetworkException in case of no connectivity
         */
        override suspend fun <T> rawNullable(callBlock: suspend () -> Response<T?>): Result<ApiSuccess<T?>> {
            return networkCallHandling(callBlock = callBlock) { it }
        }

        /**
         * Reuse the code
         */
        private suspend fun <T> networkCallHandling(
            callBlock: suspend () -> Response<T>,
            handleResponseBody: (responseBody: T?) -> T
        ): Result<ApiSuccess<T>> {
            return runCatching {
                if (!connectivity.isConnected) {
                    throw NoNetworkException(NO_INTERNET_CONNECTION_MESSAGE)
                }
                val response = try {
                    withContext(Dispatchers.IO) {
                        callBlock()
                    }
                } catch (e: JsonParseException) {
                    throw SerializationException(e)
                } catch (e: MalformedJsonException) {
                    throw SerializationException(e)
                } catch (e: IOException) {
                    throw NetworkException(e)
                }
                if (response.isSuccessful) {
                    ApiSuccess(
                        code = response.code(),
                        message = response.message(),
                        headers = response.headers().names().associateWith(response.headers()::values),
                        body = handleResponseBody(response.body())
                    )
                } else {
                    throw ApiException(response.code(), response.message(), response.errorBody()?.string())
                }
            }.onFailure { t ->
                Log.d(API_CALLER_TAG, "Exception on API call: $t")
            }
        }

    companion object {
        const val NOT_NULL_BODY_EXPECTED_MESSAGE = "Not null body response was expected!"
        const val NO_INTERNET_CONNECTION_MESSAGE = "No Internet Connection!"
        const val API_CALLER_TAG = "ApiCallerLog"
    }
}

/**
 * Exception thrown when the API call returns an empty response body, but a body is expected.
 *
 * @param message exception message
 */
class NoResponseBodyException(message: String) : IOException(message)

/**
 * Exception thrown when ConnectivityChecker isConnected() returns false.
 *
 */
class NoNetworkException(message: String) : IOException(message)

/**
 * Error representing a failed network I/O operation. Typical causes could be
 * [java.net.SocketException],
 * [java.net.SocketTimeoutException],
 * [java.net.MalformedURLException].
 */
class NetworkException(cause: Exception) : IOException(cause)

/**
 * Error representing a failed serialization/deserialization or encoding/decoding operation.
 */
class SerializationException(cause: Exception) : IOException(cause)