package com.sohohouse.seven.discover.perks

import com.sohohouse.seven.R

@Suppress("unused")
enum class BenefitContentPillar {
    ART_AND_DESIGN,
    FASHION,
    FILM_AND_ENTERTAINMENT,
    FOOD_AND_DRINK,
    IDEAS,
    MUSIC,
    PARTY,
    TRAVEL,
    WELLBEING,
    WORK;

    val label: Int
        get() = when (this) {
            ART_AND_DESIGN -> R.string.content_pillar_art_and_design
            FASHION -> R.string.content_pillar_fashion
            FILM_AND_ENTERTAINMENT -> R.string.content_pillar_fashion
            FOOD_AND_DRINK -> R.string.content_pillar_fashion
            IDEAS -> R.string.content_pillar_fashion
            MUSIC -> R.string.content_pillar_fashion
            PARTY -> R.string.content_pillar_fashion
            TRAVEL -> R.string.content_pillar_fashion
            WELLBEING -> R.string.content_pillar_fashion
            WORK -> R.string.content_pillar_fashion
        }

    companion object {
        fun forId(id: String): BenefitContentPillar? {
            return when (id) {
                "artAndDesign" -> ART_AND_DESIGN
                "fashion" -> FASHION
                "filmAndEntertainment" -> FILM_AND_ENTERTAINMENT
                "foodAndDrink" -> FOOD_AND_DRINK
                "ideas" -> IDEAS
                "music" -> MUSIC
                "party" -> PARTY
                "travel" -> TRAVEL
                "wellbeing" -> WELLBEING
                "work" -> WORK
                else -> null
            }
        }
    }

}