package com.example.mvi_clean_demo.di

import android.content.Context
import android.content.SharedPreferences
import com.example.mvi_clean_demo.common.connectivity.ConnectivityChecker
import com.example.mvi_clean_demo.common.connectivity.IConnectivityChecker
import com.example.mvi_clean_demo.common.error_handling.ErrorHandlerBroadcastService
import com.example.mvi_clean_demo.common.error_handling.IErrorHandlerBroadcastService
import com.example.mvi_clean_demo.common.retrofit.IRetrofitApiCaller
import com.example.mvi_clean_demo.common.retrofit.RetrofitApiCaller
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

    @Singleton
    @Binds
    fun bindRetrofitApiCaller(impl: RetrofitApiCaller): IRetrofitApiCaller

    @Singleton
    @Binds
    fun bindIErrorHandlerBroadcastService(impl: ErrorHandlerBroadcastService):
            IErrorHandlerBroadcastService

    @Singleton
    @Binds
    fun bindIConnectivityChecker(impl: ConnectivityChecker): IConnectivityChecker

    companion object {
        const val PREF_NAME = "MVIDemoSharedPreferences"

        @Singleton
        @Provides
        fun provideSharedPreferences(@ApplicationContext applicationContext: Context): SharedPreferences {
            return applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }
    }

}