package com.example.mvi_clean_demo.common.api

import okhttp3.Headers

/**
 * A wrapper of a successful (200..399) HTTP response.
 */
data class ApiSuccess<T>(
    val code: Int,
    val message: String? = null,
    val headers: Headers,
    val body: T
)
