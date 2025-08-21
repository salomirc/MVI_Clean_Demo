package com.example.mvi_clean_demo.sections.blog.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mvi_clean_demo.sections.blog.data.room.entities.UserEntity

@Dao
interface UserEntityDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(userEntity: UserEntity): Long

    @Query("SELECT * FROM users_table")
    suspend fun getUsers(): List<UserEntity>

    @Query("DELETE FROM users_table")
    suspend fun deleteAllUsers()
}