package com.example.mvi_clean_demo.common.helpers

fun String.getUserInitials() = this
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .map { it.first().uppercaseChar() }
        .joinToString("")