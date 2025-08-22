package com.example.mvi_clean_demo.sections.blog.presentation

import com.example.mvi_clean_demo.sections.blog.domain.model.User
import com.example.mvi_clean_demo.sections.blog.presentation.model.TierModel
import com.example.mvi_clean_demo.sections.blog.presentation.model.UserCardModel

object UsersSampleData {
    val user = User(
        address = User.Address(
            city = "Gwenborough",
            geo = User.Address.Geo(
                lat = "-37.3159",
                lng = "81.1496"
            ),
            street = "Kulas Light",
            suite = "Apt. 556",
            zipcode = "92998-3874"
        ),
        company = User.Company(
            bs = "harness real-time e-markets",
            catchPhrase = "Multi-layered client-server neural-net",
            name = "Deckow-Crist"
        ),
        email = "william.paterson@my-own-personal-domain.biz",
        id = 1,
        name = "Leanne Graham",
        phone = "1-770-736-8031 x56442",
        username = "Bret",
        website = "hildegard.org"
    )
    val models = listOf(
        UserCardModel(
            user = user,
            userInitials = "LG",
            tierModel = TierModel.Bronze
        ),
        UserCardModel(
            user = user,
            userInitials = "LG",
            tierModel = TierModel.Silver
        ),
        UserCardModel(
            user = user,
            userInitials = "LG",
            tierModel = TierModel.Gold
        ),
        UserCardModel(
            user = user,
            userInitials = "LG",
            tierModel = TierModel.Platinum
        ),
        UserCardModel(
            user = user,
            userInitials = "LG",
            tierModel = TierModel.OnePercentClub
        )
    )
}