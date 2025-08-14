package com.example.mvi_clean_demo.sections.blog.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PostEntity::class ], version = 1)
abstract class BlogDatabase: RoomDatabase() {

    abstract fun postEntityDao(): PostEntityDao

    companion object{
        const val DATABASE_NAME: String = "blog_db"
    }
}