package com.example.mvi_clean_demo.common.api

interface UIModel<T> {
    fun toUIModel(): T
}