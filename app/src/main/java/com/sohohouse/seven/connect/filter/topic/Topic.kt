package com.sohohouse.seven.connect.filter.topic

import androidx.annotation.StringRes
import com.sohohouse.seven.R

sealed class Topic(val id: String, @StringRes val title: Int) {

    companion object {
        fun forId(id: String): Topic? {
            return when (id) {
                TopicArtAndDesign.id -> TopicArtAndDesign
                Fashion.id -> Fashion
                FilmAndEntertainment.id -> FilmAndEntertainment
                FoodAndDrink.id -> FoodAndDrink
                Wellbeing.id -> Wellbeing
                Music.id -> Music
                Travel.id -> Travel
                Work.id -> Work
                U27.id -> U27
                else -> null
            }
        }
    }

    object TopicArtAndDesign : Topic("Art and Design", R.string.connect_topic_art_and_design)
    object Fashion : Topic("Fashion", R.string.connect_topic_fashion)
    object FilmAndEntertainment :
        Topic("Film and Entertainment", R.string.connect_topic_film_and_entertainment)

    object FoodAndDrink : Topic("Food and Drink", R.string.connect_topic_food_and_drink)
    object Wellbeing : Topic("Wellbeing", R.string.connect_topic_wellbeing)
    object Music : Topic("Music", R.string.connect_topic_music)
    object Travel : Topic("Travel", R.string.connect_topic_travel)
    object Work : Topic("Work", R.string.connect_topic_work)
    object U27 : Topic("U27", R.string.noticeboard_topic_tag_u27)
}