package com.example.mvi_clean_demo.common.api

interface UserInterfaceModel<T> {
    fun toUserInterfaceModel(): T
}