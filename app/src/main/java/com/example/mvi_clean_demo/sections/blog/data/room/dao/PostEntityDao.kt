package com.example.mvi_clean_demo.sections.blog.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mvi_clean_demo.sections.blog.data.room.entities.PostEntity

@Dao
interface PostEntityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(postEntity: PostEntity): Long

    @Query("SELECT * FROM posts_table")
    suspend fun getPosts(): List<PostEntity>

    @Query("SELECT * FROM posts_table WHERE userId LIKE :userId")
    suspend fun getPostsByUserId(userId: Int): List<PostEntity>

    @Query("DELETE FROM posts_table")
    suspend fun deleteAllPosts()
}