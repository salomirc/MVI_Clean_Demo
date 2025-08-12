package com.example.mvi_clean_demo.citizen.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PostEntity::class ], version = 1, exportSchema = false)
abstract class BlogDatabase: RoomDatabase() {

    abstract fun blogDao(): BlogDao

    companion object{
        const val DATABASE_NAME: String = "blog_db"
    }
}