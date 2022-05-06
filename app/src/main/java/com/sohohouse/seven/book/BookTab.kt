package com.sohohouse.seven.book

import androidx.annotation.StringRes
import com.sohohouse.seven.R

enum class BookTab(@StringRes val resId: Int) {

    BOOK_A_TABLE(R.string.book_a_table),
    HOUSE_VISIT(R.string.book_a_visit),
    EVENTS(R.string.explore_events_label),
    SCREENING(R.string.explore_cinema_label),
    GYM(R.string.book_gym_tab_label),
    BEDROOMS(R.string.bedrooms_label),
    ELECTRIC_CINEMA(R.string.book_electric_cinema)

}
