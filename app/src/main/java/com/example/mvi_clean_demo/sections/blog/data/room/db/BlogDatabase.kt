package com.example.mvi_clean_demo.sections.blog.data.room.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mvi_clean_demo.sections.blog.data.room.dao.PostEntityDao
import com.example.mvi_clean_demo.sections.blog.data.room.entities.PostEntity

@Database(entities = [PostEntity::class ], version = 1)
abstract class BlogDatabase: RoomDatabase() {

    abstract fun postEntityDao(): PostEntityDao

    companion object{
        const val DATABASE_NAME: String = "blog_db"

        fun fromFactory(context: Context): BlogDatabase {
            return Room.databaseBuilder(
                context,
                BlogDatabase::class.java,
                BlogDatabase.DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}