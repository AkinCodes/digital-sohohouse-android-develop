package com.sohohouse.seven.connect.filter.industry

import androidx.annotation.StringRes
import com.sohohouse.seven.R

sealed class Industry(val id: String, @StringRes val title: Int) {
    object Architecture : Industry("Architecture", R.string.connect_industry_Architecture)
    object Art : Industry("Art", R.string.connect_industry_Art)
    object Aviation : Industry("Aviation", R.string.connect_industry_Aviation)
    object CivilServices : Industry("Civil Services", R.string.connect_industry_CivilServices)
    object Construction : Industry("Construction", R.string.connect_industry_Construction)
    object Digital : Industry("Digital", R.string.connect_industry_Digital)
    object Education : Industry("Education", R.string.connect_industry_Education)
    object Energy : Industry("Energy", R.string.connect_industry_Energy)
    object Environment : Industry("Environment", R.string.connect_industry_Environment)
    object Fashion : Industry("Fashion", R.string.connect_industry_Fashion)
    object Film : Industry("Film", R.string.connect_industry_Film)
    object Finance : Industry("Finance", R.string.connect_industry_Finance)
    object FoodAndBeverage :
        Industry("Food And Beverage", R.string.connect_industry_Food_And_Beverage)

    object GraphicDesign : Industry("Graphic Design", R.string.connect_industry_GraphicDesign)
    object HealthAndWellness :
        Industry("Health And Wellness", R.string.connect_industry_Health_And_Wellness)

    object Hospitality : Industry("Hospitality", R.string.connect_industry_Hospitality)
    object Jewellery : Industry("Jewellery", R.string.connect_industry_Jewellery)
    object Law : Industry("Law", R.string.connect_industry_Law)
    object Literature : Industry("Literature", R.string.connect_industry_Literature)
    object Manufacturing : Industry("Manufacturing", R.string.connect_industry_Manufacturing)
    object MediaAndEntertainment :
        Industry("Media And Entertainment", R.string.connect_industry_MediaAndEntertainment)

    object Music : Industry("Music", R.string.connect_industry_Music)
}