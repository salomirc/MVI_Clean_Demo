package com.example.mvi_clean_demo.di

import android.content.Context
import android.content.SharedPreferences
import com.example.mvi_clean_demo.repositories.DataRepository
import com.example.mvi_clean_demo.repositories.IDataRepository
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

    @Singleton
    @Binds
    fun bindIRepository(impl: DataRepository): IDataRepository

    companion object {
        const val PREF_NAME = "MVIDemoSharedPreferences"

        @Singleton
        @Provides
        fun provideSharedPreferences(@ApplicationContext applicationContext: Context): SharedPreferences {
            return applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }
    }

}