package com.example.mvi_clean_demo.sections.blog.domain.model

import com.example.mvi_clean_demo.common.api.UserInterfaceModel
import com.example.mvi_clean_demo.sections.blog.presentation.model.UserCardModel

data class UserModel(
    val address: Address,
    val company: Company,
    val email: String,
    val id: Int,
    val name: String,
    val phone: String,
    val username: String,
    val website: String
): UserInterfaceModel<UserCardModel> {
    data class Address(
        val city: String,
        val geo: Geo,
        val street: String,
        val suite: String,
        val zipcode: String
    ) {
        data class Geo(
            val lat: String,
            val lng: String
        )
    }
    data class Company(
        val bs: String,
        val catchPhrase: String,
        val name: String
    )

    override fun toUserInterfaceModel(): UserCardModel {
        return UserCardModel(
            userModel = this,
            userInitials = name.take(2).uppercase()
        )
    }
}
