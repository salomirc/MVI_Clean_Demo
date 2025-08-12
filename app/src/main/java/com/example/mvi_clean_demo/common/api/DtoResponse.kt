package com.example.mvi_clean_demo.common.api

interface DtoResponse<T> {
    fun toDomainModel(): T
}