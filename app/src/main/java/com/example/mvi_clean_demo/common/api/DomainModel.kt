package com.example.mvi_clean_demo.common.api

interface DomainModel<T> {
    fun toDomainModel(): T
}

interface EntityModel<T> {
    fun toEntityModel(): T
}