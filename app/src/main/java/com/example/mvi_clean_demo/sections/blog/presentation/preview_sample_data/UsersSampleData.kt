package com.example.mvi_clean_demo.sections.blog.presentation.preview_sample_data

import com.example.mvi_clean_demo.common.helpers.getUserInitials
import com.example.mvi_clean_demo.sections.blog.domain.model.UserModel
import com.example.mvi_clean_demo.sections.blog.presentation.model.TierModel
import com.example.mvi_clean_demo.sections.blog.presentation.model.UserCardModel

object UsersSampleData {
    private val users = listOf(
        UserModel(
            id = 1,
            name = "Leanne Graham",
            username = "Bret",
            email = "Sincere@april.biz",
            address = UserModel.Address(
                street = "Kulas Light",
                suite = "Apt. 556",
                city = "Gwenborough",
                zipcode = "92998-3874",
                geo = UserModel.Address.Geo(
                    lat = "-37.3159",
                    lng = "81.1496"
                )
            ),
            phone = "1-770-736-8031 x56442",
            website = "hildegard.org",
            company = UserModel.Company(
                name = "Romaguera-Crona",
                catchPhrase = "Multi-layered client-server neural-net",
                bs = "harness real-time e-markets"
            )
        ),
        UserModel(
            id = 2,
            name = "Ervin Howell",
            username = "Antonette",
            email = "Shanna@melissa.tv",
            address = UserModel.Address(
                street = "Victor Plains",
                suite = "Suite 879",
                city = "Wisokyburgh",
                zipcode = "90566-7771",
                geo = UserModel.Address.Geo(
                    lat = "-43.9509",
                    lng = "-34.4618"
                )
            ),
            phone = "010-692-6593 x09125",
            website = "anastasia.net",
            company = UserModel.Company(
                name = "Deckow-Crist",
                catchPhrase = "Proactive didactic contingency",
                bs = "synergize scalable supply-chains"
            )
        ),
        UserModel(
            id = 3,
            name = "Clementine Bauch",
            username = "Samantha",
            email = "Nathan@yesenia.net",
            address = UserModel.Address(
                street = "Douglas Extension",
                suite = "Suite 847",
                city = "McKenziehaven",
                zipcode = "59590-4157",
                geo = UserModel.Address.Geo(
                    lat = "-68.6102",
                    lng = "-47.0653"
                )
            ),
            phone = "1-463-123-4447",
            website = "ramiro.info",
            company = UserModel.Company(
                name = "Romaguera-Jacobson",
                catchPhrase = "Face to face bifurcated interface",
                bs = "e-enable strategic applications"
            )
        ),
        UserModel(
            id = 4,
            name = "Patricia Lebsack",
            username = "Karianne",
            email = "Julianne.OConner@kory.org",
            address = UserModel.Address(
                street = "Hoeger Mall",
                suite = "Apt. 692",
                city = "South Elvis",
                zipcode = "53919-4257",
                geo = UserModel.Address.Geo(
                    lat = "29.4572",
                    lng = "-164.2990"
                )
            ),
            phone = "493-170-9623 x156",
            website = "kale.biz",
            company = UserModel.Company(
                name = "Robel-Corkery",
                catchPhrase = "Multi-tiered zero tolerance productivity",
                bs = "transition cutting-edge web services"
            )
        ),
        UserModel(
            id = 5,
            name = "Chelsey Dietrich",
            username = "Kamren",
            email = "Lucio_Hettinger@annie.ca",
            address = UserModel.Address(
                street = "Skiles Walks",
                suite = "Suite 351",
                city = "Roscoeview",
                zipcode = "33263",
                geo = UserModel.Address.Geo(
                    lat = "-31.8129",
                    lng = "62.5342"
                )
            ),
            phone = "(254)954-1289",
            website = "demarco.info",
            company = UserModel.Company(
                name = "Keebler LLC",
                catchPhrase = "User-centric fault-tolerant solution",
                bs = "revolutionize end-to-end systems"
            )
        )
    )

    val models = TierModel.tierModelImplList.mapIndexed { index, tierModel ->
        val user = users[index]
        UserCardModel(
            userModel = user,
            userInitials = user.name.getUserInitials(),
            tierModel = tierModel
        )
    }.toMutableList()
}