package com.example.mvi_clean_demo.repositories

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

interface IDataRepository {
    fun getInt(key: String, default: Int): Int
    fun putInt(key: String, value: Int)
    fun getString(key: String, default: String): String
    fun putString(key: String, value: String)
}

@Singleton
class DataRepository @Inject constructor(private val sharedPreferences: SharedPreferences): IDataRepository {

    override fun getInt(key: String, default: Int) = sharedPreferences.getInt(key, default)

    override fun putInt(key: String, value: Int) {
        sharedPreferences.edit { putInt(key, value) }
    }

    override fun getString(key: String, default: String) = sharedPreferences.getString(key, default) ?: default

    override fun putString(key: String, value: String) {
        sharedPreferences.edit { putString(key, value) }
    }
}