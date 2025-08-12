package com.example.mvi_clean_demo.common.api

/**
 * HTTP exception.
 *
 * Note: Named in this way to avoid confusion with Retrofit exception named HttpException.
 */
class ApiException(
    val code: Int,
    message: String,
    val errorBody: String? = null
) : RuntimeException("HTTP $code $message")
