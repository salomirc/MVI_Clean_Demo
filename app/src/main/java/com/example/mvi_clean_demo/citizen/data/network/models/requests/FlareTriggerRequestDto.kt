package com.example.mvi_clean_demo.citizen.data.network.models.requests

data class FlareTriggerRequestDto(
    val citizenId: String,
    val lat: String,
    val long: String
)
