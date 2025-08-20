package com.example.mvi_clean_demo.sections.blog.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mvi_clean_demo.common.api.DomainModel
import com.example.mvi_clean_demo.sections.blog.domain.model.PostEntry

@Entity(tableName = "posts_table")
data class PostEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "userId")
    val userId: Int,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "body")
    val body: String
): DomainModel<PostEntry> {

    override fun toDomainModel(): PostEntry {
        return PostEntry(
            id = id,
            userId = userId,
            title = title,
            body = body
        )
    }
}