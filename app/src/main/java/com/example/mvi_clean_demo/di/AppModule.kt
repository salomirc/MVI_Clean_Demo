package com.example.mvi_clean_demo.di

import android.content.Context
import android.content.SharedPreferences
import com.example.mvi_clean_demo.repositories.IRepository
import com.example.mvi_clean_demo.repositories.Repository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
interface AppModule {

    @Binds
    fun bindIRepository(impl: Repository): IRepository

    companion object {
        const val PREF_NAME = "MVIDemoSharedPreferences"

        @Singleton
        @Provides
        fun provideSharedPreferences(@ApplicationContext applicationContext: Context): SharedPreferences {
            return applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }
    }

}