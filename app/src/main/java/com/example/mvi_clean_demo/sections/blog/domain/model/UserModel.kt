package com.example.mvi_clean_demo.sections.blog.domain.model

import androidx.compose.runtime.Immutable
import com.example.mvi_clean_demo.common.api.UIModel
import com.example.mvi_clean_demo.common.helpers.getUserInitials
import com.example.mvi_clean_demo.sections.blog.presentation.model.TierModel
import com.example.mvi_clean_demo.sections.blog.presentation.model.UserCardModel

@Immutable
data class UserModel(
    val address: Address,
    val company: Company,
    val email: String,
    val id: Int,
    val name: String,
    val phone: String,
    val username: String,
    val website: String
): UIModel<UserCardModel> {
    @Immutable
    data class Address(
        val city: String,
        val geo: Geo,
        val street: String,
        val suite: String,
        val zipcode: String
    ) {
        @Immutable
        data class Geo(
            val lat: String,
            val lng: String
        )
    }
    @Immutable
    data class Company(
        val bs: String,
        val catchPhrase: String,
        val name: String
    )

    override fun toUIModel(): UserCardModel {
        return UserCardModel(
            userModel = this,
            userInitials = this.name.getUserInitials(),
            tierModel = TierModel.tierModelImplList.random()
        )
    }
}
