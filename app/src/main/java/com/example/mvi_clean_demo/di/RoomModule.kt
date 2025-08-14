package com.example.mvi_clean_demo.di

import android.content.Context
import androidx.room.Room
import com.example.mvi_clean_demo.sections.blog.data.room.BlogDatabase
import com.example.mvi_clean_demo.sections.blog.data.room.PostEntityDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RoomModule {

    @Singleton
    @Provides
    fun provideBlogDb(@ApplicationContext context: Context): BlogDatabase {
        return Room
            .databaseBuilder(
                context,
                BlogDatabase::class.java,
                BlogDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun providePostEntityDao(blogDatabase: BlogDatabase): PostEntityDao {
        return blogDatabase.postEntityDao()
    }
}