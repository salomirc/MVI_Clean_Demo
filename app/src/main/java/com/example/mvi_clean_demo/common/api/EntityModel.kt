package com.example.mvi_clean_demo.common.api

interface EntityModel<T> {
    fun toEntityModel(): T
}