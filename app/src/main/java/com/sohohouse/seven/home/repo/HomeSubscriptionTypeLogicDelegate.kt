package com.sohohouse.seven.home.repo

import com.sohohouse.seven.R
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.common.extensions.flow
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.home.repo.HomeSection.*
import kotlinx.coroutines.flow.Flow

enum class HomeSection {
    ROUNDEL_SHORTCUTS,
    SUGGESTED_CAROUSEL,
    HAPPENING_NOW,
    DYNAMIC_HOUSE,
    SET_UP_APP,
    BENEFITS,
    HOUSES,
    HOUSE_NOTES,

    //EVENTS_ON_DEMAND,
    TRAFFIC_LIGHTS_CONTROL_PANEL,
    HOUSE_PAY_BANNER
}

fun HomeInteractorImpl.getSectionsForSubscription(subscriptionType: SubscriptionType): List<HomeSection> {
    return when (subscriptionType) {
        SubscriptionType.FRIENDS -> {
            listOf(
                HOUSE_PAY_BANNER,
                ROUNDEL_SHORTCUTS,
                SET_UP_APP,
                BENEFITS
            )
        }
        else -> {
            mutableListOf(
                HOUSE_PAY_BANNER,
                TRAFFIC_LIGHTS_CONTROL_PANEL,
                ROUNDEL_SHORTCUTS,
                SUGGESTED_CAROUSEL,
                DYNAMIC_HOUSE,
                HAPPENING_NOW,
                //EVENTS_ON_DEMAND,
                SET_UP_APP,
                HOUSE_NOTES,
                HOUSES,
                BENEFITS
            )
        }
    }
}

fun HomeInteractorImpl.getHousesItem(subscriptionType: SubscriptionType)
        : Flow<List<BaseAdapterItem.OurHousesItem>> {
    return flow(initialValue = emptyList()) {
        val subtitle =
            if (subscriptionType == SubscriptionType.FRIENDS) R.string.our_houses_subtitle_friends
            else R.string.our_houses_subtitle
        emit(listOf(BaseAdapterItem.OurHousesItem(subtitle)))
    }
}
