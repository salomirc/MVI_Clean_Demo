package com.example.mvi_clean_demo

import android.content.SharedPreferences
import androidx.core.content.edit

class Repository(private val sharedPreferences: SharedPreferences) {

    fun getInt(key: String, default: Int) = sharedPreferences.getInt(key, default)

    fun putInt(key: String, value: Int) {
        sharedPreferences.edit { putInt(key, value) }
    }

    fun getString(key: String, default: String) = sharedPreferences.getString(key, default) ?: default

    fun putString(key: String, value: String) {
        sharedPreferences.edit { putString(key, value) }
    }
}
