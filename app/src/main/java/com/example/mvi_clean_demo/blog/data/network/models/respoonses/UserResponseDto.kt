package com.example.mvi_clean_demo.blog.data.network.models.respoonses

import com.example.mvi_clean_demo.blog.domain.model.User
import com.example.mvi_clean_demo.common.api.DtoResponse
import com.google.gson.annotations.SerializedName

data class UserResponseDto(
    @SerializedName("address") val address: Address?,
    @SerializedName("company") val company: Company?,
    @SerializedName("email") val email: String?,
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("phone") val phone: String?,
    @SerializedName("username") val username: String,
    @SerializedName("website") val website: String?
): DtoResponse<User> {
    data class Address(
        @SerializedName("city") val city: String?,
        @SerializedName("geo") val geo: Geo?,
        @SerializedName("street") val street: String?,
        @SerializedName("suite") val suite: String?,
        @SerializedName("zipcode") val zipcode: String?
    ) {
        data class Geo(
            @SerializedName("lat") val lat: String?,
            @SerializedName("lng") val lng: String?
        )
    }

    data class Company(
        @SerializedName("bs") val bs: String?,
        @SerializedName("catchPhrase") val catchPhrase: String?,
        @SerializedName("name") val name: String?
    )
    override fun toDomainModel(): User {
        return User(
            address = User.Address(
                city = address?.city ?: "",
                geo = User.Address.Geo(
                    lat = address?.geo?.lat ?: "",
                    lng = address?.geo?.lng ?: ""
                ),
                street = address?.street ?: "",
                suite = address?.suite ?: "",
                zipcode = address?.zipcode ?: ""
            ),
            company = User.Company(
                bs = company?.bs ?: "",
                catchPhrase = company?.catchPhrase ?: "",
                name = company?.name ?: ""
            ),
            email = email ?: "",
            id = id,
            name = name ?: "",
            phone = phone ?: "",
            username = username,
            website = website ?: ""
        )
    }
}


