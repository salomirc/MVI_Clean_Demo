package com.example.mvi_clean_demo.citizen.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BlogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(postEntity: PostEntity): Long

    @Query("SELECT * FROM posts")
    suspend fun getPosts(): List<PostEntity>
}