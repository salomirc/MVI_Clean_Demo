package com.example.mvi_clean_demo.common.api

/**
 * A wrapper of a successful (200..399) HTTP response.
 */
data class ApiSuccess<T>(
    val code: Int,
    val message: String? = null,
    val headers: Map<String, List<String>> = emptyMap(),
    val body: T
)
