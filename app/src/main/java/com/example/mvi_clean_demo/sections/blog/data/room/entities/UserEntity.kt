package com.example.mvi_clean_demo.sections.blog.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mvi_clean_demo.common.api.DomainModel
import com.example.mvi_clean_demo.sections.blog.domain.model.User

@Entity(tableName = "users_table")
data class UserEntity(

    @Embedded(prefix = "address_")
    val address: Address,

    @Embedded(prefix = "company_")
    val company: Company,

    @ColumnInfo(name = "email")
    val email: String,

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "phone")
    val phone: String,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "website")
    val website: String,

    @ColumnInfo(name = "token")
    var token: String? = null

): DomainModel<User> {
    data class Address(

        @ColumnInfo(name = "city")
        val city: String,

        @Embedded(prefix = "geo_")
        val geo: Geo,

        @ColumnInfo(name = "street")
        val street: String,

        @ColumnInfo(name = "suite")
        val suite: String,

        @ColumnInfo(name = "zipcode")
        val zipcode: String
    ) {
        data class Geo(

            @ColumnInfo(name = "lat")
            val lat: String,

            @ColumnInfo(name = "lng")
            val lng: String
        )
    }

    data class Company(

        @ColumnInfo(name = "bs")
        val bs: String,

        @ColumnInfo(name = "catchPhrase")
        val catchPhrase: String,

        @ColumnInfo(name = "name")
        val name: String
    )

    override fun toDomainModel(): User {
        return User(
            address = User.Address(
                city = address.city,
                geo = User.Address.Geo(
                    lat = address.geo.lat,
                    lng = address.geo.lng
                ),
                street = address.street,
                suite = address.suite,
                zipcode = address.zipcode
            ),
            company = User.Company(
                bs = company.bs,
                catchPhrase = company.catchPhrase,
                name = company.name
            ),
            email = email,
            id = id,
            name = name,
            phone = phone,
            username = username,
            website = website
        )
    }
}

// Optional Create a relationship wrapper (POJO)
// Room supports one-to-many via @Relation.
// Define a new data class:

//data class UserWithPosts(
//    @Embedded val user: UserEntity,
//    @Relation(
//        parentColumn = "id",
//        entityColumn = "userId"
//    )
//    val posts: List<PostEntity>
//)


// Optional Update your DAO
//@Dao
//interface UserDao {
//    @Transaction
//    @Query("SELECT * FROM users_table WHERE id = :userId")
//    suspend fun getUserWithPosts(userId: Int): UserWithPosts
//
//    @Transaction
//    @Query("SELECT * FROM users_table")
//    suspend fun getAllUsersWithPosts(): List<UserWithPosts>
//}
