package com.example.mvi_clean_demo.sections.blog.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.mvi_clean_demo.common.api.DomainModel
import com.example.mvi_clean_demo.sections.blog.domain.model.PostEntryModel

@Entity(
    tableName = "posts_table",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE // ✅ delete posts when user is deleted
        )
    ],
    indices = [Index(value = ["userId"])] // ✅ improves query performance
)
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
): DomainModel<PostEntryModel> {

    override fun toDomainModel(): PostEntryModel {
        return PostEntryModel(
            id = id,
            userId = userId,
            title = title,
            body = body
        )
    }
}