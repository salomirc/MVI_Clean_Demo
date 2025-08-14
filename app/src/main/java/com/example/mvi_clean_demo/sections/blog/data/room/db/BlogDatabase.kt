package com.example.mvi_clean_demo.sections.blog.data.room.db

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mvi_clean_demo.sections.blog.data.room.dao.PostEntityDao
import com.example.mvi_clean_demo.sections.blog.data.room.dao.UserEntityDao
import com.example.mvi_clean_demo.sections.blog.data.room.entities.PostEntity
import com.example.mvi_clean_demo.sections.blog.data.room.entities.UserEntity

@Database(
    entities = [PostEntity::class, UserEntity::class],
    version = 3, // ✅ increment version
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2), // existing
        AutoMigration(from = 2, to = 3) // ✅ new
    ]
)
abstract class BlogDatabase: RoomDatabase() {

    abstract fun postEntityDao(): PostEntityDao
    abstract fun userEntityDao(): UserEntityDao

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