package com.example.mvi_clean_demo.sections.blog.presentation.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import com.example.mvi_clean_demo.R
import com.example.mvi_clean_demo.sections.blog.domain.model.UserModel
import com.example.mvi_clean_demo.theme.BlackSurface
import com.example.mvi_clean_demo.theme.BronzeSurface
import com.example.mvi_clean_demo.theme.GoldSurface
import com.example.mvi_clean_demo.theme.InitialsBgSurfaceLight
import com.example.mvi_clean_demo.theme.OnePercentClubSurface
import com.example.mvi_clean_demo.theme.PlatinumSurface
import com.example.mvi_clean_demo.theme.SilverSurface

@Stable
data class UserCardModel(
    val userModel: UserModel,
    var isExpanded: Boolean = true,
    val userInitials : String,
    val tierModel: TierModel = TierModel.Gold
)

@Immutable
sealed interface TierModel {
    @get:DrawableRes
    val tierIconRes: Int
    @get:DrawableRes
    val tierLineRes: Int
    val tierSurfaceColor: Color
    val tierTextIconColor: Color
        get() = BlackSurface
    val tierTitle: String

    data object Bronze: TierModel {
        override val tierIconRes = R.drawable.bronze_tier_icon
        override val tierLineRes = R.drawable.bronze_line
        override val tierSurfaceColor = BronzeSurface
        override val tierTitle = "Bronze"
    }

    data object Silver: TierModel {
        override val tierIconRes = R.drawable.silver_tier_icon
        override val tierLineRes = R.drawable.silver_line
        override val tierSurfaceColor = SilverSurface
        override val tierTitle = "Silver"
    }

    data object Gold: TierModel {
        override val tierIconRes = R.drawable.gold_tier_icon
        override val tierLineRes = R.drawable.gold_line
        override val tierSurfaceColor = GoldSurface
        override val tierTitle = "Gold"
    }

    data object Platinum: TierModel {
        override val tierIconRes = R.drawable.platinum_tier_icon
        override val tierLineRes = R.drawable.platinum_line
        override val tierSurfaceColor = PlatinumSurface
        override val tierTitle = "Platinum"
    }

    data object OnePercentClub: TierModel {
        override val tierIconRes = R.drawable.one_percent_club_tier_icon
        override val tierLineRes = R.drawable.one_percent_club_line
        override val tierSurfaceColor = OnePercentClubSurface
        override val tierTextIconColor: Color
            get() = InitialsBgSurfaceLight
        override val tierTitle = "1% Club"
    }

    companion object {
        val tierModelImplList = listOf(Bronze, Gold, Silver, Platinum, OnePercentClub)
    }
}