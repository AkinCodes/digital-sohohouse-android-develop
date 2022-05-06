package com.sohohouse.seven.book

import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import javax.inject.Inject

class BookViewInfo @Inject constructor(userManager: UserManager) {

    val tabs: List<BookTab> = when (userManager.subscriptionType) {
        SubscriptionType.FRIENDS -> listOf(
            BookTab.EVENTS,
            BookTab.BOOK_A_TABLE,
            BookTab.BEDROOMS,
            BookTab.ELECTRIC_CINEMA,
            BookTab.SCREENING
        )
        else -> listOf(
            BookTab.HOUSE_VISIT,
            BookTab.EVENTS,
            BookTab.BOOK_A_TABLE,
            BookTab.BEDROOMS,
            BookTab.SCREENING,
            BookTab.GYM
        )
    }
}